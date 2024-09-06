package site.liangbai.lbapi.economy.impl

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import site.liangbai.lbapi.economy.EconomyProvider

class VaultProvider : EconomyProvider<OfflinePlayer, Double> {
    private val economy by lazy {
        Bukkit.getServer().servicesManager.getRegistration(Economy::class.java).provider
    }

    override fun checkBalance(player: OfflinePlayer, balance: Double): Boolean {
        return economy.has(player, balance)
    }

    override fun getBalance(player: OfflinePlayer): Double {
        return economy.getBalance(player)
    }

    override fun withdraw(player: OfflinePlayer, balance: Double): Boolean {
        return economy.withdrawPlayer(player, balance).type == EconomyResponse.ResponseType.SUCCESS
    }

    override fun deposit(player: OfflinePlayer, balance: Double): Boolean {
        return economy.depositPlayer(player, balance).type == EconomyResponse.ResponseType.SUCCESS
    }
}