package site.liangbai.lbapi.economy.impl

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import site.liangbai.lbapi.economy.EconomyProvider

class PlaceholderProvider(val checker: String, val withdrawCommand: String, val depositCommand: String): EconomyProvider<Double> {
    override fun checkBalance(player: Player, balance: Double): Boolean {
        return getBalance(player) >= balance
    }

    override fun getBalance(player: Player): Double {
        return PlaceholderAPI.setPlaceholders(player, checker).toDouble()
    }

    override fun withdraw(player: Player, balance: Double): Boolean {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            PlaceholderAPI.setPlaceholders(player, withdrawCommand)
        )
        return true
    }

    override fun deposit(player: Player, balance: Double): Boolean {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            PlaceholderAPI.setPlaceholders(player, depositCommand)
        )
        return true
    }
}