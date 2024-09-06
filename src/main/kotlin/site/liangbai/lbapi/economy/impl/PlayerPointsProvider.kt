package site.liangbai.lbapi.economy.impl

import org.black_ixx.playerpoints.PlayerPoints
import org.bukkit.Bukkit
import site.liangbai.lbapi.economy.EconomyProvider

class PlayerPointsProvider : EconomyProvider<String, Double> {
    private val api by lazy {
        (Bukkit.getPluginManager().getPlugin("PlayerPoints") as PlayerPoints).api
    }

    override fun checkBalance(player: String, balance: Double): Boolean {
        return getBalance(player) >= balance
    }

    override fun getBalance(player: String): Double {
        return api.look(player).toDouble()
    }

    override fun withdraw(player: String, balance: Double): Boolean {
        return api.take(player, balance.toInt())
    }

    override fun deposit(player: String, balance: Double): Boolean {
        return api.give(player, balance.toInt())
    }
}