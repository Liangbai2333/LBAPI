package site.liangbai.lbapi.config.mapper.type

import site.liangbai.lbapi.config.ConfigMapper
import site.liangbai.lbapi.serverbridge.RedisConfig
import taboolib.library.configuration.ConfigurationSection

object RedisLoader : ConfigMapper<ConfigurationSection, RedisConfig> {
    override fun map(original: ConfigurationSection): RedisConfig {
        val pass = original.getString("pass")?.let { it.ifEmpty { null } }
        val auth = original.getString("auth")

        return RedisConfig(
            original.getString("host", "localhost")!!,
            original.getInt("port", 6379),
            // 有user必须要密码，没user密码传递给auth
            if (auth.isNullOrEmpty()) null else pass,
            auth?.let { it.ifEmpty { pass } } ?: pass,
            original.getString("prefix", "server")!!
        )
    }
}