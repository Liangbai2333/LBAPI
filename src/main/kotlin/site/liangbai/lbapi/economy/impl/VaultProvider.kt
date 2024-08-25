package site.liangbai.lbapi.economy.impl

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import site.liangbai.lbapi.economy.EconomyProvider

class VaultProvider : EconomyProvider<Double> {
    private val economy by lazy {
        Bukkit.getServer().servicesManager.getRegistration(Economy::class.java).provider
    }

    override fun checkBalance(player: Player, balance: Double): Boolean {
        return economy.has(player, balance)
    }

    override fun getBalance(player: Player): Double {
        return economy.getBalance(player)
    }

    override fun withdraw(player: Player, balance: Double): Boolean {
        return economy.withdrawPlayer(player, balance).type == EconomyResponse.ResponseType.SUCCESS
    }

    override fun deposit(player: Player, balance: Double): Boolean {
        return economy.depositPlayer(player, balance).type == EconomyResponse.ResponseType.SUCCESS
    }
}