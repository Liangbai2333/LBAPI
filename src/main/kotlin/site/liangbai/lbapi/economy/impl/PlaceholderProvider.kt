package site.liangbai.lbapi.economy.impl

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import site.liangbai.lbapi.economy.EconomyProvider

class PlaceholderProvider(val checker: String, val withdrawCommand: String, val depositCommand: String): EconomyProvider<OfflinePlayer, Double> {
    override fun checkBalance(player: OfflinePlayer, balance: Double): Boolean {
        return getBalance(player) >= balance
    }

    override fun getBalance(player: OfflinePlayer): Double {
        return PlaceholderAPI.setPlaceholders(player, checker).toDouble()
    }

    override fun withdraw(player: OfflinePlayer, balance: Double): Boolean {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            PlaceholderAPI.setPlaceholders(player, withdrawCommand)
        )
        return true
    }

    override fun deposit(player: OfflinePlayer, balance: Double): Boolean {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            PlaceholderAPI.setPlaceholders(player, depositCommand)
        )
        return true
    }
}