package site.liangbai.lbapi.config.mapper.economy.api

import site.liangbai.lbapi.economy.EconomyManager

data class PayInfo(val economy: String, val value: Any)

fun List<PayInfo>.register(channel: String) {
    EconomyManager.registerChannelWithDefaultValues(channel, *map { it.economy to it.value }.toTypedArray())
}

fun List<PayInfo>.unregister(channel: String) {
    EconomyManager.unregisterChannel(channel)
}