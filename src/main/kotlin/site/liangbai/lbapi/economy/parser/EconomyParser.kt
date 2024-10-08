package site.liangbai.lbapi.economy.parser

import site.liangbai.lbapi.economy.parser.impl.*

object EconomyParser {
    private val parsers = mutableMapOf<String, Parser<*>>()
    private val parserSupports = mutableMapOf<Parser<*>, MutableList<String>>()

    init {
        registerParser(VaultParser(), "vault", "money")
        registerParser(PlayerPointsParser(), "player_points", "pp", "points")
        registerParser(NyEParser(), "nye")
        registerParser(PlayerPointsParser(), "placeholder", "papi")
        registerParser(ContainsItemParser(), "contains_item", "item")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> parseEconomy(economy: String): Pair<String, T> {
        val type = economy.substringBefore(":")
        val data = economy.substringAfter(":")
        val parser = findParser(type)
        val value = parser.parse(data)

        var original = parserSupports[parser]!![0]

        if (type.equals("nye", ignoreCase = true)) {
            original = "${original}_${(value as Pair<String, Double>).first}"
            return original to value.second as T
        } else if (type.equals("placeholder", ignoreCase = true)) {
            original = "${original}_${(value as PlaceholderInfo).name}"
        }

        return original to value as T
    }

    fun findParser(type: String) = parsers[type]!!

    fun registerParser(parser: Parser<*>, vararg economies: String) {
        economies.forEach {
            parsers[it] = parser
        }
        if (parser !in parserSupports) {
            parserSupports[parser] = mutableListOf(*economies)
        } else {
            parserSupports[parser]!!.addAll(economies)
        }
    }
}