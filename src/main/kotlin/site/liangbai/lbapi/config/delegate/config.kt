package site.liangbai.lbapi.config.delegate

import site.liangbai.lbapi.config.ConfigManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class config<T>(private var node: String = "") : ReadWriteProperty<Any?, T?>  {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val get = ConfigManager.getBind(thisRef)[n] ?: return null
        return get as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        conf[n] = value
    }
}