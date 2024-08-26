package site.liangbai.lbapi.storage.converter.impl

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToElement
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.storage.converter.IConverter
import site.liangbai.lbapi.util.toItemTag
import site.liangbai.lbapi.util.toJsonElement
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

@PlatformSide(Platform.BUKKIT)
class ItemStackConverter : IConverter<ItemStack> {
    override fun convertToElement(value: ItemStack): JsonElement {
        return JsonObject().also {
            value.serialize()
                .apply { remove("meta") }
                .forEach { (k, v) ->
                    it.add(k, v.convertToElement())
                }
            it.add("nbt", value.getItemTag().toJsonElement())
        }
    }

    override fun convertFromString(data: JsonElement): ItemStack {
        val map = mutableMapOf<String, Any>()
        val obj = data.asJsonObject
        obj.entrySet().forEach {
            if (it.key != "nbt") {
                map[it.key] = translateType(it.value)
            }
        }

        return ItemStack.deserialize(map).setItemTag(obj["nbt"].toItemTag())
    }

    private fun translateType(it: JsonElement): Any {
        return if (it.isJsonPrimitive) {
            val primitive = it.asJsonPrimitive
            if (primitive.isNumber) {
                it.asNumber
            } else if (primitive.isBoolean) {
                it.asBoolean
            } else if (primitive.isString) {
                it.asString
            } else it.asString.convertToEntity()
        } else {
            it.toString().convertToEntity()
        }
    }
}