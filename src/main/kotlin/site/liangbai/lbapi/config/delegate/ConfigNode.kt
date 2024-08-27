@file:JvmName("ConfigNodeKt")

package site.liangbai.lbapi.config.delegate

import site.liangbai.lbapi.config.ConfigManager
import site.liangbai.lbapi.config.ConfigMapper
import site.liangbai.lbapi.util.getObjectInstance
import taboolib.library.configuration.ConfigurationSection
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class ConfigNode<T>(private var node: String = "", private val mapper: ConfigMapper<T>? = null) :
    ReadWriteProperty<Any?, T> {
    private var cached: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        if (cached != null) {
            if (thisRef in ConfigManager.flushCache) {
                cached = null
                ConfigManager.flushCache.remove(thisRef)
            } else {
                return cached!!
            }
        }
        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        val get = conf[n]!!
        val returnValue: T? = if (mapper != null) {
            mapper.map(get as ConfigurationSection)
        } else {
            get as T
        }

        return returnValue.also { cached = it }!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        conf[n] = value
    }
}

fun <T> config(mapperClass: Class<out ConfigMapper<T>>? = null, node: String = ""): ConfigNode<T> {
    return ConfigNode(node, mapperClass?.getObjectInstance())
}