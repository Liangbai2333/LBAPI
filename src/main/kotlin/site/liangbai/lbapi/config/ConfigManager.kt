package site.liangbai.lbapi.config

import taboolib.module.configuration.ConfigFile

object ConfigManager {
    val binds = mutableMapOf<Any, ConfigFile>()

    fun Any.bindTo(configFile: ConfigFile) {
        binds[this] = configFile
    }

    fun getBind(obj: Any): ConfigFile {
        return binds[obj] ?: throw IllegalArgumentException("bind does not exist")
    }
}