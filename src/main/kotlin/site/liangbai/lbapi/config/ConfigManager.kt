package site.liangbai.lbapi.config

import site.liangbai.lbapi.config.delegate.ConfigNode
import site.liangbai.lbapi.config.delegate.NullableConfigNode
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.reflex.ReflexClass
import taboolib.module.configuration.ConfigFile

object ConfigManager {
    val binds = mutableMapOf<Any, ConfigFile>()
    val bindsSection = mutableMapOf<Any, ConfigurationSection>()

    fun Any.bindTo(configFile: ConfigFile, section: String = "") {
        binds[this] = configFile
        if (section.isNotBlank()) {
            bindsSection[this] = configFile.getConfigurationSection(section)!!
        }
    }

    fun Any.flushConf() {
        binds[this]!!.reload()
        if (this in bindsSection) {
            bindsSection[this] = binds[this]!!.getConfigurationSection(bindsSection[this]!!.name)!!
        }
        ReflexClass.of(this::class.java).structure.fields.forEach {
            val fieldObj = it.get(this)
            if (fieldObj is ConfigNode<*, *>) {
                fieldObj.notifyChanged()
            }
            if (fieldObj is NullableConfigNode<*, *>) {
                fieldObj.notifyChanged()
            }
        }
    }

    fun getBind(obj: Any): ConfigurationSection {
        if (obj in bindsSection) {
            return bindsSection[obj]!!
        }
        return binds[obj] ?: throw IllegalArgumentException("bind does not exist")
    }

    fun saveBind(obj: Any) {
        if (obj in bindsSection) {
            val sec = bindsSection[this]!!
            binds[this]!![sec.name] = sec
        }
        binds[this]!!.saveToFile()
    }
}