package site.liangbai.lbapi.economy.parser

interface Parser<T> {
    fun parse(source: String): T
}