package site.liangbai.lbapi.database.converter.impl.bean

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import site.liangbai.lbapi.database.converter.ConverterManager.convertToElement
import site.liangbai.lbapi.database.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.database.converter.IConverter
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.library.reflex.ReflexClass

class BeanConverter<T : Bean> : IConverter<T> {
    override fun convertToElement(value: T): JsonElement {
        return JsonObject()
            .apply {
                addProperty("\$primitive_class_type\$", value.javaClass.name)
                ReflexClass.of(value.javaClass).structure
                    .fields.forEach {
                        val v = it.get(value) ?: add(it.name, null)

                        when (v) {
                            is Number -> addProperty(it.name, v)
                            is String -> addProperty(it.name, v)
                            is Boolean -> addProperty(it.name, v)
                            is Char -> addProperty(it.name, v)
                            else -> add(it.name, v.convertToElement())
                        }
                    }
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun convertFromString(data: JsonElement): T {
        val js = data.asJsonObject
        val clz = Class.forName(js["\$primitive_class_type\$"]!!.asString) ?: throw ClassNotFoundException("Cannot find bean class: ${js["\$primitive_class_type\$"]!!.asString}")
        val obj = clz.unsafeInstance()

        js.remove("\$primitive_class_type\$")
        js.keySet().forEach { name ->
            val property = js[name] ?: return@forEach

            if (property.isJsonPrimitive) {
                val primitive = property.asJsonPrimitive
                if (primitive.isString) {
                    obj.setProperty(name, primitive.asString)
                } else if (primitive.isBoolean) {
                    obj.setProperty(name, primitive.asBoolean)
                } else if (primitive.isNumber) {
                    obj.setProperty(name, primitive.asNumber)
                }
            } else if (property.isJsonNull) {
                obj.setProperty(name, null)
            } else obj.setProperty(name, property.convertToEntity())
        }

        return obj as T
    }
}