package site.liangbai.lbapi.gui

import org.bukkit.Material
import site.liangbai.lbapi.gui.api.GuiIconInfo
import site.liangbai.lbapi.gui.api.GuiInfo
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial

object GuiLoader {
    fun loadGuiFromSection(config: ConfigurationSection): GuiInfo {
        val icons = mutableMapOf<Char, GuiIconInfo>()
        val layout = config.getStringList("layout")
        val iconsSection = config.getConfigurationSection("icons")!!

        iconsSection.getKeys(false).forEach { key ->
            val iconCharSection = iconsSection.getConfigurationSection(key)!!

            val material = iconCharSection.getString("material")!!
            var xMaterial: XMaterial? = null
            val materialType: Material
            val m = XMaterial.matchXMaterial(material)
            if (m.isPresent) {
                xMaterial = m.get()
                materialType = xMaterial.parseMaterial()!!
            } else {
                materialType = Material.getMaterial(material)!!
            }

            icons[key.first()] = GuiIconInfo(
                materialType,
                iconCharSection.getInt("model-data", -1),
                iconCharSection.getString("name")!!,
                iconCharSection.getStringList("lore"),
                xMaterial
            )
        }

        return GuiInfo(layout, icons)
    }
}