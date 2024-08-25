package site.liangbai.lbapi.config

import site.liangbai.lbapi.gui.GuiLoader
import site.liangbai.lbapi.gui.api.GuiInfo
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.ConfigFile

object ConfigManager {
    val binds = mutableMapOf<Any, ConfigFile>()

    fun Any.bindTo(configFile: ConfigFile) {
        binds[this] = configFile
    }

    fun getBind(obj: Any): ConfigFile {
        return binds[obj] ?: throw IllegalArgumentException("bind does not exist")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> ConfigurationSection.transfer(): T {
        val keys = this.getKeys(false)
        if ("icons" in keys && "layout" in keys) {
            return GuiLoader.loadGuiFromSection(this) as T
        }

        throw IllegalArgumentException("transfer does not exist")
    }

    fun GuiInfo.transfer(): ConfigurationSection {
        TODO()
    }
}