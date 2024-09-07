package site.liangbai.lbapi.nms

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
}