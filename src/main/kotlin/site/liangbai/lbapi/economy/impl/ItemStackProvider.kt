package site.liangbai.lbapi.economy.impl

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.economy.EconomyProvider
import site.liangbai.lbapi.util.addItemNatural
import site.liangbai.lbapi.util.reduceItem

class ItemStackProvider : EconomyProvider<ItemStack> {
    override fun checkBalance(player: Player, balance: ItemStack): Boolean {
        return player.inventory.containsAtLeast(balance, balance.amount)
    }

    override fun getBalance(player: Player): ItemStack {
        throw IllegalStateException("Invalid invoke")
    }

    override fun deposit(player: Player, balance: ItemStack): Boolean {
        return player.inventory.reduceItem(balance)
    }

    override fun withdraw(player: Player, balance: ItemStack): Boolean {
        return player.addItemNatural(balance)
    }
}