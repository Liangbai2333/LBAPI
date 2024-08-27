package site.liangbai.lbapi.config

import taboolib.library.configuration.ConfigurationSection

// only object
interface ConfigMapper<A, B> {
    fun map(original: A): B
}