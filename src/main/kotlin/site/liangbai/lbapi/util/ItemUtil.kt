package site.liangbai.lbapi.util

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.nms.NMS
import taboolib.module.chat.colored

fun Player.addItemNatural(vararg items: ItemStack?): Boolean {
    val leftover = inventory.addItem(*items)
    return if (leftover.isNotEmpty()) {
        leftover.values.forEach {
            world.dropItem(location, it)
        }
        false
    } else true
}

fun Inventory.reduceItem(itemStack: ItemStack): Boolean {
    var b = false
    var amount = itemStack.amount
    var index = firstWithoutAmount(itemStack)
    while (index != -1) {
        val target = getItem(index)!!
        if (target.amount >= amount) {
            target.amount -= amount
            b = true
            setItem(index, if (target.amount == 0) null else target)
            break
        } else {
            amount -= target.amount
            target.amount = 0
            setItem(index, null)
        }

        index = firstWithoutAmount(itemStack)
    }

    return b
}

fun Inventory.firstWithoutAmount(item: ItemStack?): Int {
    if (item == null) return -1

    var index = -1

    for (i in 0 until size) {
        val target = getItem(i)
        if (target != null && target.isSimilar(item)) {
            index = i
            break
        }
    }

    return index
}

fun Inventory.findTitle() = NMS.INSTANCE.getTitleName(this)

fun ItemStack.hasLore(lore: String, colored: Boolean = true): Boolean {
    return this.itemMeta?.lore?.joinToString("")?.contains(if (colored) lore.colored() else lore) ?: false
}

fun ItemStack.hasName(name: String, colored: Boolean = true): Boolean {
    return itemMeta?.displayName?.contains(if (colored) name.colored() else name) ?: false
}