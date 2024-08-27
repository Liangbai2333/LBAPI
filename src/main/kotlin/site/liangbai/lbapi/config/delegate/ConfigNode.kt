@file:JvmName("ConfigNodeKt")

package site.liangbai.lbapi.config.delegate

import site.liangbai.lbapi.config.ConfigManager
import site.liangbai.lbapi.config.ConfigMapper
import site.liangbai.lbapi.util.getObjectInstance
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class ConfigNode<A, B>(private var node: String = "", private val mapper: ConfigMapper<A, B>? = null) :
    ReadWriteProperty<Any?, B> {
    private var cached: B? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): B {
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
        val returnValue: B? = if (mapper != null) {
            mapper.map(get as A)
        } else {
            get as B
        }

        return returnValue.also { cached = it }!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: B) {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        conf[n] = value
    }
}

fun <A, B> config(mapperClass: Class<out ConfigMapper<A, B>>? = null, node: String = ""): ConfigNode<A, B> {
    return ConfigNode(node, mapperClass?.getObjectInstance())
}