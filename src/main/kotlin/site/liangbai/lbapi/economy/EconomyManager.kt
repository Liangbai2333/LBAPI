package site.liangbai.lbapi.economy

import com.mc9y.nyeconomy.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import site.liangbai.lbapi.economy.impl.*

object EconomyManager {
    private val economyMap = mutableMapOf<EconomyProvider<*>, Economy>()
    private val economyNameMap = mutableMapOf<String, EconomyProvider<*>>()
    private val channelMap = mutableMapOf<String, MutableList<EconomyProvider<*>>>()

    private val channelValueMap = mutableMapOf<String, MutableMap<EconomyProvider<*>, Any>>()

    private val channelWithPlayerUniqueIdMap = mutableMapOf<String, MutableMap<String, MutableList<EconomyProvider<*>>>>()
    private val channelWithPlayerUniqueIdValueMap = mutableMapOf<String, MutableMap<String, MutableMap<EconomyProvider<*>, Any>>>()

    init {
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

    fun register(economy: Economy, provider: EconomyProvider<*>) {
        economyMap[provider] = economy
        economyNameMap[economy.name] = provider
    }

    fun registerPlaceholder(name: String, provider: PlaceholderProvider) {
        register(economyOf("placeholder_$name"), provider)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getEconomyByName(name: String): EconomyProvider<T> {
        return economyNameMap[name] as? EconomyProvider<T> ?: throw NullPointerException("Economy not found")
    }

    fun registerChannel(plugin: Plugin, vararg economies: String) {
        channelMap[plugin.name] = mutableListOf()

        for (economy in economies) {
            if (economyNameMap.containsKey(economy)) {
                channelMap[plugin.name]!!.add(getEconomyByName<Any>(economy))
            }
        }
    }

    fun registerChannelWithDefaultValues(plugin: Plugin, vararg economyWithValues: Pair<String, Any>) {
        channelValueMap[plugin.name] = mutableMapOf()

        registerChannel(plugin, *economyWithValues.map { it.first }.toTypedArray())

        for (economyWithValue in economyWithValues) {
            if (economyNameMap.containsKey(economyWithValue.first)) {
                channelValueMap[plugin.name]!![getEconomyByName<Any>(economyWithValue.first)] = economyWithValue.second
            }
        }
    }

    fun registerPlayerChannel(plugin: Plugin, player: String, vararg economies: String) {
        if (plugin.name !in channelWithPlayerUniqueIdMap) {
            channelWithPlayerUniqueIdMap[plugin.name] = mutableMapOf()
        }
        val playerUniqueIdMap = channelWithPlayerUniqueIdMap[plugin.name]!!
        playerUniqueIdMap[player] = mutableListOf()

        for (economy in economies) {
            if (economyNameMap.containsKey(economy)) {
                playerUniqueIdMap[player]!!.add(getEconomyByName<Any>(economy))
            }
        }
    }

    fun registerPlayerChannelWithDefaultValues(plugin: Plugin, player: String, vararg economyWithValues: Pair<String, Any>) {
        if (plugin.name !in channelWithPlayerUniqueIdValueMap) {
            channelWithPlayerUniqueIdValueMap[plugin.name] = mutableMapOf()
        }
        val playerUniqueIdValueMap = channelWithPlayerUniqueIdValueMap[plugin.name]!!
        playerUniqueIdValueMap[player] = mutableMapOf()

        registerPlayerChannel(plugin, player, *economyWithValues.map { it.first }.toTypedArray())

        for (economyWithValue in economyWithValues) {
            if (economyNameMap.containsKey(economyWithValue.first)) {
                playerUniqueIdValueMap[player]!![getEconomyByName<Any>(economyWithValue.first)] = economyWithValue.second
            }
        }
    }

    // Return not enough
    @Suppress("UNCHECKED_CAST")
    fun <T> checkWithChannel(plugin: Plugin, checker: (EconomyProvider<T>) -> Boolean): Economy? {
        var economy: Economy? = null
        var b = true
        channelMap[plugin.name]?.forEach {
            if (!b) return@forEach
            if (!checker(it as EconomyProvider<T>)) {
                economy = economyMap[it]
                b = false
            }
        }

        return economy
    }

    fun checkChannelDefaultValues(plugin: Plugin, player: Player): Economy? {
        val values = channelValueMap[player.name]!!

        return checkWithChannel(plugin) {
            it.checkBalance(player, values[it]!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> checkWithPlayerChannel(plugin: Plugin, player: String, checker: (EconomyProvider<T>) -> Boolean): Economy? {
        var economy: Economy? = null
        var b = true
        channelWithPlayerUniqueIdMap[plugin.name]!![player]?.forEach {
            if (!b) return@forEach
            if (!checker(it as EconomyProvider<T>)) {
                economy = economyMap[it]
                b = false
            }
        }

        return economy
    }

    fun checkPlayerChannelDefaultValues(plugin: Plugin, player: Player): Economy? {
        val values = channelWithPlayerUniqueIdValueMap[plugin.name]!![player.name]!!

        return checkWithPlayerChannel(plugin, player.name) {
            it.checkBalance(player, values[it]!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> withChannel(plugin: Plugin, func: (EconomyProvider<T>) -> Unit) {
        channelMap[plugin.name]?.forEach {
            func(it as EconomyProvider<T>)
        }
    }

    fun withdrawDefaultValues(plugin: Plugin, player: Player) {
        val values = channelValueMap[player.name]!!

        withChannel(plugin) {
            it.withdraw(player, values[it]!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> withPlayerChannel(plugin: Plugin, player: String, func: (EconomyProvider<T>) -> Unit) {
        channelWithPlayerUniqueIdMap[plugin.name]!![player]?.forEach {
            func(it as EconomyProvider<T>)
        }
    }

    fun withdrawPlayerDefaultValues(plugin: Plugin, player: Player) {
        val values = channelWithPlayerUniqueIdValueMap[plugin.name]!![player.name]!!

        withPlayerChannel(plugin, player.name) {
            it.withdraw(player, values[it]!!)
        }
    }
}