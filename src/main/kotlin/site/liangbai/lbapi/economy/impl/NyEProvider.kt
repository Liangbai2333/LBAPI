package site.liangbai.lbapi.economy.impl

import com.mc9y.nyeconomy.api.NyEconomyAPI
import org.bukkit.entity.Player
import site.liangbai.lbapi.economy.EconomyProvider

// Map<String, NyeProvider> 判断Nye类型.
class NyEProvider(val type: String): EconomyProvider<Double> {
    private val api by lazy {
        NyEconomyAPI.getInstance()
    }

    override fun checkBalance(player: Player, balance: Double): Boolean {
        return getBalance(player) >= balance
    }

    override fun getBalance(player: Player): Double {
        return api.getBalance(type, player.name).toDouble()
    }

    override fun withdraw(player: Player, balance: Double): Boolean {
        api.withdraw(type, player.name, balance.toInt())
        return true
    }

    override fun deposit(player: Player, balance: Double): Boolean {
        api.deposit(type, player.name, balance.toInt())
        return true
    }
}