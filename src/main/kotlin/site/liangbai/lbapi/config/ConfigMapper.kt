package site.liangbai.lbapi.config

import taboolib.library.configuration.ConfigurationSection

// only object
interface ConfigMapper<T> {
    fun map(original: ConfigurationSection): T
}