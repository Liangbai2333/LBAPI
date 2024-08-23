package site.liangbai.lbapi.economy.impl

import org.black_ixx.playerpoints.PlayerPoints
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import site.liangbai.lbapi.economy.EconomyProvider

class PlayerPointsProvider : EconomyProvider<Double> {
    private val api by lazy {
        (Bukkit.getPluginManager().getPlugin("PlayerPoints") as PlayerPoints).api
    }

    override fun checkBalance(player: Player, balance: Double): Boolean {
        return getBalance(player) >= balance
    }

    override fun getBalance(player: Player): Double {
        return api.look(player.uniqueId).toDouble()
    }

    override fun withdraw(player: Player, balance: Double): Boolean {
        return api.take(player.uniqueId, balance.toInt())
    }

    override fun deposit(player: Player, balance: Double): Boolean {
        return api.give(player.uniqueId, balance.toInt())
    }
}