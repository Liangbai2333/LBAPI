package site.liangbai.lbapi.nms

import net.minecraft.item.ItemStack
import org.bukkit.inventory.Inventory
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy

abstract class NMS {
    companion object {
        val INSTANCE by unsafeLazy {
            nmsProxy<NMS>()
        }
    }

    abstract fun getTitleName(inventory: Inventory): String

    abstract fun getNBTClass(): Class<*>

    abstract fun toBukkitItem(original: ItemStack): org.bukkit.inventory.ItemStack

    abstract fun toNMSItem(original: org.bukkit.inventory.ItemStack): ItemStack
}