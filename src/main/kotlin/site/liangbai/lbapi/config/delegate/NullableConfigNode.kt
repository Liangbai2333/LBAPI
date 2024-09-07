package site.liangbai.lbapi.config.delegate

import site.liangbai.lbapi.config.ConfigManager
import site.liangbai.lbapi.config.ConfigMapper
import site.liangbai.lbapi.util.getObjectInstance
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class NullableConfigNode<A, B>(private var node: String = "", private val mapper: ConfigMapper<A, B>? = null) : ReadWriteProperty<Any?, B?>  {
    private var cached: B? = null

    private var needToChange = false

    fun notifyChanged() {
        needToChange = true
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): B? {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        if (cached != null) {
            if (needToChange) {
                cached = null
                needToChange = false
            } else {
                return cached!!
            }
        }
        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        val get = conf[n] ?: return null

        val returnValue: B?
        returnValue = if (mapper != null) {
            mapper.map(get as A)
        } else {
            get as B
        }

        return returnValue.also { cached = it }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: B?) {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        conf[n] = value
    }
}

fun <B> configNullable(mapperClass: Class<out ConfigMapper<*, B>>? = null, node: String = ""): NullableConfigNode<*, B> {
    return NullableConfigNode(node, mapperClass?.getObjectInstance())
}