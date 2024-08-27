package site.liangbai.lbapi.economy.parser.impl

import site.liangbai.lbapi.economy.parser.Parser

// containsItem:name:lore:amount
class ContainsItemParser : Parser<ItemInfo> {
    override fun parse(source: String): ItemInfo {
        val list = source.split(":").dropLastWhile { it.isEmpty() }
        val name = list[0]
        val lore = list[1]
        val amount = list[2].toInt()
        val material = if (list.size > 3) list[3] else ""
        val damage = if (list.size > 4) list[4].toInt() else 0
        return ItemInfo(name, lore, amount, material, damage)
    }
}

class ItemInfo(val name: String, val lore: String, val amount: Int, val material: String, val damage: Int)