package site.liangbai.lbapi.economy.parser.impl

import site.liangbai.lbapi.economy.parser.Parser

class PlaceholderParser : Parser<PlaceholderInfo> {
    override fun parse(source: String): PlaceholderInfo {
        val list = source.split(":").dropLastWhile { it.isEmpty() }
        val name = list[0]
        val checker = list[1]
        val withdrawCommand = list[2]
        val depositCommand = if (list.size > 3) list[3] else ""
        return PlaceholderInfo(name, checker, withdrawCommand, depositCommand)
    }
}

class PlaceholderInfo(val name: String, val checker: String, val withdrawCommand: String, val depositCommand: String)