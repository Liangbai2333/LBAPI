package site.liangbai.lbapi.economy.parser.impl

import site.liangbai.lbapi.economy.parser.Parser

class NyEParser : Parser<Pair<String, Double>> {
    override fun parse(source: String): Pair<String, Double> {
        val list = source.split(":").dropLastWhile { it.isEmpty() }
        val type = list[0]
        val price = list[1].toDouble()
        return Pair(type, price)
    }
}