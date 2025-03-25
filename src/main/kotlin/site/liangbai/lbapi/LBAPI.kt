package site.liangbai.lbapi

import site.liangbai.lbapi.economy.EconomyManager
import site.liangbai.lbapi.serverbridge.BridgeRegistry
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object LBAPI {
    fun initialize() {
        EconomyManager.initialize()
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisable() {
        if (BridgeRegistry.isRedisCreated()) {
            BridgeRegistry.redis.close()
        }
    }
}