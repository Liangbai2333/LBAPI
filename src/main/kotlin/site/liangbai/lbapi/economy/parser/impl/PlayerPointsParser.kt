package site.liangbai.lbapi.economy.parser.impl

import site.liangbai.lbapi.economy.parser.Parser

class PlayerPointsParser : Parser<Double> {
    override fun parse(source: String): Double {
        return source.toDouble()
    }
}