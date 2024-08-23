package site.liangbai.lbapi.economy

import org.bukkit.entity.Player

interface EconomyProvider<T> {
    fun checkBalance(player: Player, balance: T): Boolean

    fun getBalance(player: Player): T

    fun withdraw(player: Player, balance: T): Boolean

    fun deposit(player: Player, balance: T): Boolean
}