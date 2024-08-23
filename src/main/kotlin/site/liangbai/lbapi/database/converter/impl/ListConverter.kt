package site.liangbai.lbapi.database.converter.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import site.liangbai.lbapi.database.converter.ConverterManager.convertToElement
import site.liangbai.lbapi.database.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.database.converter.IConverter
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide

@PlatformSide(Platform.BUKKIT)
class ListConverter<T> : IConverter<MutableList<T>> {
    private val parser = JsonParser()

    override fun convertToElement(value: MutableList<T>): JsonElement {
        val jsonArray = JsonArray()

        value.forEach {
            if (it is String || it is Number || it is Boolean) {
                if (it is Number) {
                    jsonArray.add(JsonPrimitive(it))
                } else if (it is Boolean) {
                    jsonArray.add(JsonPrimitive(it))
                } else if (it is String) {
                    jsonArray.add(JsonPrimitive(it))
                } else if (it is Char) {
                    jsonArray.add(JsonPrimitive(it))
                } else jsonArray.add(JsonPrimitive(it.toString()))
            } else {
                jsonArray.add(it?.convertToElement())
            }
        }

        return jsonArray
    }

    @Suppress("UNCHECKED_CAST")
    override fun convertFromString(data: JsonElement): MutableList<T> {
        val jsonArray = data.asJsonArray
        val list = mutableListOf<T>()

        jsonArray.forEach {
            if (it.isJsonPrimitive) {
                val primitive = it.asJsonPrimitive
                if (primitive.isNumber) {
                    list.add(it.asNumber as T)
                } else if (primitive.isBoolean) {
                    list.add(it.asBoolean as T)
                } else if (primitive.isString) {
                    list.add(it.asString as T)
                } else list.add(it.asString.convertToEntity())
            } else {
                list.add(it.toString().convertToEntity())
            }
        }

        return list
    }
}