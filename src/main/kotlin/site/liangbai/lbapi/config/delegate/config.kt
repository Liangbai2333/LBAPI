package site.liangbai.lbapi.config.delegate

import site.liangbai.lbapi.config.ConfigManager
import site.liangbai.lbapi.config.ConfigManager.transfer
import site.liangbai.lbapi.gui.api.GuiInfo
import taboolib.library.configuration.ConfigurationSection
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class config<T>(private var node: String = "") : ReadWriteProperty<Any?, T?>  {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val get = ConfigManager.getBind(thisRef)[n] ?: return null
        val cls = (property.returnType.classifier as KClass<*>).java
        if (!cls.isAssignableFrom(get.javaClass)) {
            return (get as ConfigurationSection).transfer()
        }
        return get as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (thisRef == null) throw IllegalArgumentException("bind access")

        val n = node.ifEmpty { property.name }
        val conf = ConfigManager.getBind(thisRef)
        val cls = (property.returnType.classifier as KClass<*>).java
        // TODO
        if (cls == GuiInfo::class.java) {
            return
        } else {
            conf[n] = value
        }
    }
}