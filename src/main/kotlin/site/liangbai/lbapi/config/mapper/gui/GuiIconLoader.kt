package site.liangbai.lbapi.config.mapper.gui

import org.bukkit.Material
import site.liangbai.lbapi.config.ConfigMapper
import site.liangbai.lbapi.config.mapper.gui.api.GuiIconInfo
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial

object GuiIconLoader : ConfigMapper<ConfigurationSection, GuiIconInfo> {
    fun loadItemIconFromConfSec(conf: ConfigurationSection): GuiIconInfo {
        val material = conf.getString("material")!!
        var xMaterial: XMaterial? = null
        val materialType: Material
        val m = XMaterial.matchXMaterial(material)
        if (m.isPresent) {
            xMaterial = m.get()
            materialType = xMaterial.parseMaterial()!!
        } else {
            materialType = Material.getMaterial(material)!!
        }

        return GuiIconInfo(
            materialType,
            conf.getInt("model-data", -1),
            conf.getString("name")!!,
            conf.getStringList("lore"),
            conf.getInt("damage", 0),
            conf.getInt("amount", 1),
            xMaterial,
            null
        )
    }
    
    override fun map(original: ConfigurationSection): GuiIconInfo {
        return loadItemIconFromConfSec(original)
    }
}