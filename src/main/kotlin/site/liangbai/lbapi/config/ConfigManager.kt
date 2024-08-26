package site.liangbai.lbapi.config

import taboolib.module.configuration.ConfigFile

object ConfigManager {
    val binds = mutableMapOf<Any, ConfigFile>()
    val flushCache = mutableListOf<Any>()

    fun Any.bindTo(configFile: ConfigFile) {
        binds[this] = configFile
    }

    fun Any.flushConf() {
        binds[this]!!.reload()
        flushCache.add(this)
    }

    fun getBind(obj: Any): ConfigFile {
        return binds[obj] ?: throw IllegalArgumentException("bind does not exist")
    }
}