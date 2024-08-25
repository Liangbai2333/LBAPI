package site.liangbai.lbapi.economy.parser.impl

import site.liangbai.lbapi.economy.parser.Parser

class PlaceholderParser : Parser<Pair<String, Triple<String, String, String>>> {
    override fun parse(source: String): Pair<String, Triple<String, String, String>> {
        val list = source.split(":").dropLastWhile { it.isEmpty() }
        val name = list[0]
        val checker = list[1]
        val withdrawCommand = list[2]
        val depositCommand = list[3]
        return Pair(name, Triple(checker, withdrawCommand, depositCommand))
    }
}