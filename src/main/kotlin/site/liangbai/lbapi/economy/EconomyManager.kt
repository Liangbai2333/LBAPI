package site.liangbai.lbapi.economy

import com.mc9y.nyeconomy.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import site.liangbai.lbapi.economy.impl.*
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object EconomyManager {
    private val economyMap = mutableMapOf<EconomyProvider<*>, Economy>()
    private val economyNameMap = mutableMapOf<String, EconomyProvider<*>>()
    private var channelMap = mutableListOf<EconomyProvider<*>>()

    private var channelValueMap = mutableMapOf<EconomyProvider<*>, Any>()

    private val channelWithPlayerNameMap = mutableMapOf<String, MutableList<EconomyProvider<*>>>()
    private val channelWithPlayerNameValueMap = mutableMapOf<String, MutableMap<EconomyProvider<*>, Any>>()

    @Awake(LifeCycle.ACTIVE)
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

    fun registerChannel(vararg economies: String) {
        channelMap = mutableListOf()

        for (economy in economies) {
            if (economyNameMap.containsKey(economy)) {
                channelMap.add(getEconomyByName<Any>(economy))
            }
        }
    }

    fun registerChannelWithDefaultValues(vararg economyWithValues: Pair<String, Any>) {
        channelValueMap = mutableMapOf()

        registerChannel(*economyWithValues.map { it.first }.toTypedArray())

        for (economyWithValue in economyWithValues) {
            if (economyNameMap.containsKey(economyWithValue.first)) {
                channelValueMap[getEconomyByName<Any>(economyWithValue.first)] = economyWithValue.second
            }
        }
    }

    fun registerPlayerChannel(player: String, vararg economies: String) {
        if (player !in channelWithPlayerNameMap) {
            channelWithPlayerNameMap[player] = mutableListOf()
        }
        val playerUniqueIdMap = channelWithPlayerNameMap[player]!!

        for (economy in economies) {
            if (economyNameMap.containsKey(economy)) {
                playerUniqueIdMap.add(getEconomyByName<Any>(economy))
            }
        }
    }

    fun registerPlayerChannelWithDefaultValues(player: String, vararg economyWithValues: Pair<String, Any>) {
        if (player !in channelWithPlayerNameValueMap) {
            channelWithPlayerNameValueMap[player] = mutableMapOf()
        }
        val playerUniqueIdValueMap = channelWithPlayerNameValueMap[player]!!
        registerPlayerChannel(player, *economyWithValues.map { it.first }.toTypedArray())

        for (economyWithValue in economyWithValues) {
            if (economyNameMap.containsKey(economyWithValue.first)) {
                playerUniqueIdValueMap[getEconomyByName<Any>(economyWithValue.first)] = economyWithValue.second
            }
        }
    }

    // Return not enough
    @Suppress("UNCHECKED_CAST")
    fun <T> checkWithChannel(checker: (EconomyProvider<T>) -> Boolean): Economy? {
        var economy: Economy? = null
        var b = true
        channelMap.forEach {
            if (!b) return@forEach
            if (!checker(it as EconomyProvider<T>)) {
                economy = economyMap[it]
                b = false
            }
        }

        return economy
    }

    fun checkChannelDefaultValues(player: Player): Economy? {
        return checkWithChannel {
            it.checkBalance(player, channelValueMap[it]!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> checkWithPlayerChannel(player: String, checker: (EconomyProvider<T>) -> Boolean): Economy? {
        var economy: Economy? = null
        var b = true
        channelWithPlayerNameMap[player]?.forEach {
            if (!b) return@forEach
            if (!checker(it as EconomyProvider<T>)) {
                economy = economyMap[it]
                b = false
            }
        }

        return economy
    }

    fun checkPlayerChannelDefaultValues(player: Player): Economy? {
        val values = channelWithPlayerNameValueMap[player.name]!!

        return checkWithPlayerChannel(player.name) {
            it.checkBalance(player, values[it]!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> withChannel(func: (EconomyProvider<T>) -> Unit) {
        channelMap.forEach {
            func(it as EconomyProvider<T>)
        }
    }

    fun withdrawDefaultValues(player: Player) {
        withChannel {
            it.withdraw(player, channelValueMap[it]!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> withPlayerChannel(player: String, func: (EconomyProvider<T>) -> Unit) {
        channelWithPlayerNameMap[player]?.forEach {
            func(it as EconomyProvider<T>)
        }
    }

    fun withdrawPlayerDefaultValues(player: Player) {
        val values = channelWithPlayerNameValueMap[player.name]!!

        withPlayerChannel(player.name) {
            it.withdraw(player, values[it]!!)
        }
    }
}