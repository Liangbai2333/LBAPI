package site.liangbai.lbapi.economy

import org.bukkit.entity.Player

interface EconomyProvider<PLAYER, T> {
    fun checkBalance(player: PLAYER, balance: T): Boolean

    fun getBalance(player: PLAYER): T

    fun withdraw(player: PLAYER, balance: T): Boolean

    fun deposit(player: PLAYER, balance: T): Boolean
}