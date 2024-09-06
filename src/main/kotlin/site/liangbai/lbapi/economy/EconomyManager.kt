package site.liangbai.lbapi.economy

import com.mc9y.nyeconomy.Main
import org.bukkit.Bukkit
import site.liangbai.lbapi.economy.impl.*

// 暂时, 即将全面重构
object EconomyManager {
    private val economyMap = mutableMapOf<EconomyProvider<*, *>, Economy>()
    private val economyNameMap = mutableMapOf<String, EconomyProvider<*, *>>()
    private var channelMap = mutableMapOf<String, MutableList<EconomyProvider<*, *>>>()

    private var channelValueMap = mutableMapOf<String, MutableMap<EconomyProvider<*, *>, Any>>()

    fun initialize() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            register(economyOf("vault"), VaultProvider())
        }
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
            register(economyOf("player_points"), PlayerPointsProvider())
        }
        if (Bukkit.getPluginManager().getPlugin("NyEconomy") != null) {
            Main.getInstance().vaults.forEach {
                register(economyOf("nye_$it"), NyEProvider(it))
            }
        }
        register(economyOf("item"), ItemStackProvider())
        register(economyOf("multi_item"), MultiItemStackProvider())
        register(economyOf("contains_item"), ContainsItemStackProvider())
    }

    fun register(economy: Economy, provider: EconomyProvider<*, *>) {
        economyMap[provider] = economy
        economyNameMap[economy.name] = provider
    }

    fun registerPlaceholder(name: String, provider: PlaceholderProvider) {
        register(economyOf("placeholder_$name"), provider)
    }

    @Suppress("UNCHECKED_CAST")
    fun <PLAYER, T> getEconomyByName(name: String): EconomyProvider<PLAYER, T> {
        return economyNameMap[name] as? EconomyProvider<PLAYER, T> ?: throw NullPointerException("Economy not found")
    }

    fun registerChannel(channel: String, vararg economies: String) {
        channelMap[channel] = mutableListOf()

        for (economy in economies) {
            if (economyNameMap.containsKey(economy)) {
                channelMap[channel]!!.add(getEconomyByName<Any, Any>(economy))
            }
        }
    }

    fun registerChannelWithDefaultValues(channel: String, vararg economyWithValues: Pair<String, Any>) {
        channelValueMap[channel] = mutableMapOf()

        registerChannel(channel, *economyWithValues.map { it.first }.toTypedArray())

        for (economyWithValue in economyWithValues) {
            if (economyNameMap.containsKey(economyWithValue.first)) {
                channelValueMap[channel]!![getEconomyByName<Any, Any>(economyWithValue.first)] = economyWithValue.second
            }
        }
    }

    fun unregisterChannel(channel: String) {
        channelMap.remove(channel)
        channelValueMap.remove(channel)
    }

    // Return not enough
    @Suppress("UNCHECKED_CAST")
    fun <PLAYER, T> checkWithChannel(channel: String, checker: (EconomyProvider<PLAYER, T>) -> Boolean): Economy? {
        var economy: Economy? = null
        var b = true
        channelMap[channel]!!.forEach {
            if (!b) return@forEach
            if (!checker(it as EconomyProvider<PLAYER, T>)) {
                economy = economyMap[it]
                b = false
            }
        }

        return economy
    }

    fun checkChannelDefaultValues(channel: String, player: String): Economy? {
        return checkWithChannel(channel) {
            it.checkBalance(it.adaptPlayer(player), channelValueMap[channel]!![it]!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <PLAYER, T> withChannel(channel: String, func: (EconomyProvider<PLAYER, T>) -> Unit) {
        channelMap[channel]!!.forEach {
            func(it as EconomyProvider<PLAYER, T>)
        }
    }

    fun withdrawDefaultValues(channel: String, player: String) {
        withChannel(channel) {
            it.withdraw(it.adaptPlayer(player), channelValueMap[channel]!![it]!!)
        }
    }

    fun getChannelMap(channel: String): MutableList<EconomyProvider<*, *>>? {
        return channelMap[channel]
    }

    fun getChannelMapWithDefaultValues(channel: String): MutableMap<EconomyProvider<*, *>, Any>? {
        return channelValueMap[channel]
    }

    fun EconomyProvider<*, *>.adaptPlayer(player: String): Any {
        return when (this) {
            is VaultProvider -> Bukkit.getOfflinePlayer(player)
            is PlayerPointsProvider -> player
            is PlaceholderProvider -> Bukkit.getOfflinePlayer(player)
            is NyEProvider -> player
            is ItemStackProvider -> Bukkit.getPlayerExact(player)
            is MultiItemStackProvider -> Bukkit.getPlayerExact(player)
            is ContainsItemStackProvider -> Bukkit.getPlayerExact(player)
            else -> player
        }
    }
}