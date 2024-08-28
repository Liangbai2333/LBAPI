package site.liangbai.lbapi.storage.converter.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import site.liangbai.lbapi.storage.converter.IConverter
import java.util.UUID

class UUIDConverter : IConverter<UUID> {
    override fun convertToElement(value: UUID): JsonElement {
        return JsonPrimitive(value.toString())
    }

    override fun convertFromString(data: JsonElement): UUID {
        return UUID.fromString(data.asString)
    }
}