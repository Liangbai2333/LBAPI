package site.liangbai.lbapi.economy.parser.impl

import site.liangbai.lbapi.economy.parser.Parser

// vault/money:100
class VaultParser : Parser<Double> {
    override fun parse(source: String): Double {
        return source.toDouble()
    }
}