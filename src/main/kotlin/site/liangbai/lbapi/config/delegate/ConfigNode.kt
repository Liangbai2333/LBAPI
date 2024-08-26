package site.liangbai.lbapi.config.delegate

import site.liangbai.lbapi.config.ConfigManager
import site.liangbai.lbapi.config.ConfigMapper
import site.liangbai.lbapi.util.getObjectInstance
import taboolib.library.configuration.ConfigurationSection
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class ConfigNode<T>(private var node: String = "", private val mapper: ConfigMapper<T>? = null) : ReadWriteProperty<Any?, T?>  {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        val get = conf[n] ?: return null
        if (mapper != null) {
            return mapper.map(get as ConfigurationSection)
        }
        return get as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        conf[n] = value
    }
}

fun <T> config(code: String = "", mapperClass: Class<out ConfigMapper<T>>? = null): ConfigNode<T> {
    return ConfigNode(code, mapperClass?.getObjectInstance())
}