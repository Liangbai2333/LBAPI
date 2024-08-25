package site.liangbai.lbapi.economy.parser.impl

import site.liangbai.lbapi.economy.parser.Parser

// containsItem:name:lore:amount
class ContainsItemParser : Parser<Triple<String, String, Int>> {
    override fun parse(source: String): Triple<String, String, Int> {
        val list = source.split(":").dropLastWhile { it.isEmpty() }
        val name = list[0]
        val lore = list[1]
        val amount = list[2].toInt()
        return Triple(name, lore, amount)
    }
}