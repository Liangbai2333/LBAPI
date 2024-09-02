package site.liangbai.lbapi.config.mapper.economy

import site.liangbai.lbapi.config.ConfigMapper
import site.liangbai.lbapi.config.mapper.economy.api.PayInfo
import site.liangbai.lbapi.economy.parser.EconomyParser

object PayListMapper : ConfigMapper<List<String>, List<PayInfo>> {
    override fun map(original: List<String>): List<PayInfo> {
        val list = mutableListOf<PayInfo>()
        original.forEach {
            val info = EconomyParser.parseEconomy<Any>(it)
            list.add(PayInfo(info.first, info.second))
        }
        return list
    }
}