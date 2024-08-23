package site.liangbai.lbapi.economy.impl

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.economy.EconomyProvider
import site.liangbai.lbapi.util.addItemNatural
import site.liangbai.lbapi.util.reduceItem

class MultiItemStackProvider : EconomyProvider<List<ItemStack>> {
    override fun checkBalance(player: Player, balance: List<ItemStack>): Boolean {
        return balance.all { it in player.inventory }
    }

    override fun getBalance(player: Player): List<ItemStack> {
        throw IllegalStateException("Invalid invoke")
    }

    override fun deposit(player: Player, balance: List<ItemStack>): Boolean {
        return balance.all { player.inventory.reduceItem(it) }
    }

    override fun withdraw(player: Player, balance: List<ItemStack>): Boolean {
        return balance.all { player.addItemNatural(it) }
    }
}