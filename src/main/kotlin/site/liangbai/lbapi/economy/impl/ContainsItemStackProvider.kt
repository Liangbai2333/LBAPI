package site.liangbai.lbapi.economy.impl

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.economy.EconomyProvider
import site.liangbai.lbapi.economy.parser.impl.ItemInfo
import site.liangbai.lbapi.util.hasLore
import site.liangbai.lbapi.util.hasName

class ContainsItemStackProvider : EconomyProvider<ItemInfo> {
    override fun checkBalance(player: Player, balance: ItemInfo): Boolean {
        return player.getItemStackByItemInfo(balance).map { it.value.amount }.sum() >= balance.amount
    }

    override fun getBalance(player: Player): ItemInfo {
        throw IllegalStateException("Invalid invoke")
    }

    override fun deposit(player: Player, balance: ItemInfo): Boolean {
        throw IllegalStateException("Invalid invoke")
    }

    override fun withdraw(player: Player, balance: ItemInfo): Boolean {
        var amount = balance.amount

        player.getItemStackByItemInfo(balance).forEach {
            val index = it.key
            val item = it.value

            if (item.amount >= amount) {
                item.amount -= amount
                player.inventory.setItem(index, if (item.amount == 0) null else item)
                return true
            } else {
                amount -= item.amount
                item.amount = 0
                player.inventory.setItem(index, null)
            }
        }
        return false
    }

    private fun Player.getItemStackByItemInfo(info: ItemInfo): Map<Int, ItemStack> {
        if ((info.name.isEmpty() && info.lore.isEmpty() && info.material.isEmpty()) || info.amount < 1) {
            throw IllegalArgumentException("Invalid invoke")
        }

        val map = mutableMapOf<Int, ItemStack>()

        for (i in 0 until inventory.size) {
            var b1: Boolean
            var b2: Boolean
            var b3: Boolean
            val item = inventory.getItem(i)
            b1 = if (info.name.isEmpty()) {
                true
            } else {
                item.hasName(info.name)
            }
            b2 = if (info.lore.isEmpty()) {
                true
            } else {
                item.hasLore(info.lore)
            }
            b3 = if (info.material.isEmpty()) {
                true
            } else {
                if (info.damage > 0) {
                    item.durability.toInt() == info.damage && item.type.name.equals(info.material, true)
                } else {
                    item.type.name.equals(info.material, true)
                }
            }

            if (b1 && b2 && b3) {
                map[i] = item
            }
        }

        return map
    }
}