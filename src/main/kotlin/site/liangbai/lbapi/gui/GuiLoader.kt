package site.liangbai.lbapi.gui

import site.liangbai.lbapi.config.ConfigMapper
import site.liangbai.lbapi.gui.api.GuiIconInfo
import site.liangbai.lbapi.gui.api.GuiInfo
import taboolib.library.configuration.ConfigurationSection

object GuiLoader : ConfigMapper<GuiInfo> {
    fun loadGuiFromSection(config: ConfigurationSection): GuiInfo {
        val icons = mutableMapOf<Char, GuiIconInfo>()
        val layout = config.getStringList("layout")
        val iconsSection = config.getConfigurationSection("icons")!!

        iconsSection.getKeys(false).forEach { key ->
            val iconCharSection = iconsSection.getConfigurationSection(key)!!
            icons[key.first()] = GuiIconLoader.loadItemIconFromConfSec(iconCharSection)
        }

        return GuiInfo(config.getString("title"), layout, icons)
    }

    override fun map(original: ConfigurationSection): GuiInfo {
        return loadGuiFromSection(original)
    }
}