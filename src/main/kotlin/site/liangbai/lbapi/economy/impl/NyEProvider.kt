package site.liangbai.lbapi.economy.impl

import com.mc9y.nyeconomy.api.NyEconomyAPI
import org.bukkit.entity.Player
import site.liangbai.lbapi.economy.EconomyProvider

// Map<String, NyeProvider> 判断Nye类型.
class NyEProvider(val type: String): EconomyProvider<String, Double> {
    private val api by lazy {
        NyEconomyAPI.getInstance()
    }

    override fun checkBalance(player: String, balance: Double): Boolean {
        return getBalance(player) >= balance
    }

    override fun getBalance(player: String): Double {
        return api.getBalance(type, player).toDouble()
    }

    override fun withdraw(player: String, balance: Double): Boolean {
        api.withdraw(type, player, balance.toInt())
        return true
    }

    override fun deposit(player: String, balance: Double): Boolean {
        api.deposit(type, player, balance.toInt())
        return true
    }
}