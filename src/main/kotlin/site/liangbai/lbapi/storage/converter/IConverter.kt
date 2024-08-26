package site.liangbai.lbapi.storage.converter

import com.google.gson.JsonElement

interface IConverter<X> {
    fun convertToElement(value: X): JsonElement
    fun convertFromString(data: JsonElement): X
}