package site.liangbai.lbapi.storage.converter.impl

import com.google.gson.Gson
import com.google.gson.JsonElement
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToElement
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.storage.converter.IConverter
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide

@PlatformSide(Platform.BUKKIT)
class ValueMapConverter<V> : IConverter<Map<String, V>> {
    private val gson = Gson()

    override fun convertToElement(value: Map<String, V>): JsonElement {
        return gson.toJsonTree(
            value.mapValues { it.value?.convertToElement() }
            , Map::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun convertFromString(data: JsonElement): Map<String, V> {
        return gson.fromJson(data, Map::class.java)
            .mapValues {
                gson.toJsonTree(it.value).convertToEntity<V>()
            } as Map<String, V>
    }
}