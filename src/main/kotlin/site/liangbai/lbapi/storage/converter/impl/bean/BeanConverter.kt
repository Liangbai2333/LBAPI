package site.liangbai.lbapi.storage.converter.impl.bean

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToElement
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.storage.converter.IConverter
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.library.reflex.ReflexClass

class BeanConverter<T : Bean> : IConverter<T> {
    override fun convertToElement(value: T): JsonElement {
        return convertToElementPrimitive(value, value.javaClass.name)
    }

    private fun convertToElementPrimitive(value: T, target: String): JsonElement {
        return JsonObject()
            .apply {
                addProperty("\$primitive_class_type\$", target)
                val ref = ReflexClass.of(Class.forName(target))
                val superclass = ref.superclass
                if (superclass != null && superclass.structure.owner != Any::class.java) {
                    add("\$superclass\$", convertToElementPrimitive(value, superclass.structure.owner.name))
                }
                ref.structure
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

    private fun setObjWithClass(js: JsonObject, obj: Any, clz: Class<*>) {
        if (js.has("\$superclass\$")) {
            val superJs = js["\$superclass\$"].asJsonObject
            val superClz = Class.forName(superJs["\$primitive_class_type\$"]!!.asString) ?: throw ClassNotFoundException("Cannot find bean class: ${superJs["\$primitive_class_type\$"]!!.asString}")
            superJs.remove("\$primitive_class_type\$")
            setObjWithClass(js["\$superclass\$"].asJsonObject, obj, superClz)
            js.remove("\$superclass\$")
        }

        js.getProperty<Map<String, JsonElement>>("members")!!.keys.forEach { name ->
            val property = js[name] ?: return@forEach

            if (property.isJsonPrimitive) {
                val primitive = property.asJsonPrimitive
                if (primitive.isString) {
                    obj.setProperty(name, primitive.asString)
                } else if (primitive.isBoolean) {
                    obj.setProperty(name, primitive.asBoolean)
                } else if (primitive.isNumber) {
                    ReflexClass.of(clz).structure.fields.first { it.name == name }.also {
                        if (it.fieldType == Long::class.java) {
                            obj.setProperty(name, primitive.asLong)
                        } else if (it.fieldType == Double::class.java) {
                            obj.setProperty(name, primitive.asDouble)
                        } else if (it.fieldType == Int::class.java) {
                            obj.setProperty(name, primitive.asInt)
                        } else if (it.fieldType == Short::class.java) {
                            obj.setProperty(name, primitive.asShort)
                        } else if (it.fieldType == Byte::class.java) {
                            obj.setProperty(name, primitive.asByte)
                        } else if (it.fieldType == Float::class.java) {
                            obj.setProperty(name, primitive.asFloat)
                        }
                    }
                }
            } else if (property.isJsonNull) {
                obj.setProperty(name, null)
            } else obj.setProperty(name, property.convertToEntity())
        }
    }


    @Suppress("UNCHECKED_CAST")
    override fun convertFromString(data: JsonElement): T {
        val js = data.asJsonObject
        val clz = Class.forName(js["\$primitive_class_type\$"]!!.asString) ?: throw ClassNotFoundException("Cannot find bean class: ${js["\$primitive_class_type\$"]!!.asString}")
        val obj = clz.unsafeInstance()

        js.remove("\$primitive_class_type\$")
        setObjWithClass(js, obj, clz)
        return obj as T
    }
}