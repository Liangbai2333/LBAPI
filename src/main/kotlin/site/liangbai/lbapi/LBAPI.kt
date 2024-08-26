package site.liangbai.lbapi

import site.liangbai.lbapi.economy.EconomyManager

object LBAPI {
    fun initialize() {
        EconomyManager.initialize()
    }
}