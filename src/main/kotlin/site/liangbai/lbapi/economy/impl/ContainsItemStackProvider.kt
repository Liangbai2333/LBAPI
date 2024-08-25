package site.liangbai.lbapi.economy.impl

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.economy.EconomyProvider
import site.liangbai.lbapi.util.hasLore
import site.liangbai.lbapi.util.hasName

//1 name 2 lore 3 amount
class ContainsItemStackProvider : EconomyProvider<Triple<String, String, Int>> {
    override fun checkBalance(player: Player, balance: Triple<String, String, Int>): Boolean {
        return player.getItemStackByNameAndLore(balance.first, balance.second).map { it.value.amount }.sum() >= balance.third
    }

    override fun getBalance(player: Player): Triple<String, String, Int> {
        throw IllegalStateException("Invalid invoke")
    }

    override fun deposit(player: Player, balance: Triple<String, String, Int>): Boolean {
        throw IllegalStateException("Invalid invoke")
    }

    override fun withdraw(player: Player, balance: Triple<String, String, Int>): Boolean {
        var amount = balance.third

        player.getItemStackByNameAndLore(balance.first, balance.second).forEach {
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

    private fun Player.getItemStackByNameAndLore(name: String, lore: String): Map<Int, ItemStack> {
        if (name.isEmpty() && lore.isEmpty()) {
            throw IllegalArgumentException("Invalid invoke")
        }

        val map = mutableMapOf<Int, ItemStack>()

        for (i in 0 until inventory.size) {
            var b1 = false
            var b2 = false
            val item = inventory.getItem(i)
            b1 = if (name.isEmpty()) {
                true
            } else {
                item.hasName(name)
            }
            b2 = if (lore.isEmpty()) {
                true
            } else {
                item.hasLore(lore)
            }

            if (b1 && b2) {
                map[i] = item
            }
        }

        return map
    }
}