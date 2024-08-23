package site.liangbai.lbapi.database.converter

import com.google.gson.JsonElement

interface IConverter<X> {
    fun convertToElement(value: X): JsonElement
    fun convertFromString(data: JsonElement): X
}