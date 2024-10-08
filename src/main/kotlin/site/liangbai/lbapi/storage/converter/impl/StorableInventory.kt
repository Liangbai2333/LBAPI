package site.liangbai.lbapi.storage.converter.impl

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToElement
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.storage.converter.IConverter
import site.liangbai.lbapi.nms.NMS
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Chest
import taboolib.platform.util.isNotAir

class StorableInventory<T : Inventory> : IConverter<T> {
    override fun convertToElement(value: T): JsonElement {
        return JsonObject()
            .also {
                it.addProperty("title", NMS.INSTANCE.getTitleName(value))

                val map = mutableMapOf<String, ItemStack>()
                for (i in 0 until value.size) {
                    val item = value.getItem(i)
                    if (item != null && item.isNotAir()) {
                        map[i.toString()] = value.getItem(i)
                    }
                }
                it.addProperty("rows", (value.size / 9))
                it.add("items", map.convertToElement())
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun convertFromString(data: JsonElement): T {
        val js = data.asJsonObject
        val title = js["title"]!!.asString
        val rows = js["rows"]!!.asInt
        val items = js["items"]!!.convertToEntity<Map<String, ItemStack>>()

        return buildMenu<Chest>(title) {
            rows(rows)
        }.also {
            items.forEach { (slot, item) ->
                it.setItem(slot.toInt(), item)
            }
        } as T
    }
}