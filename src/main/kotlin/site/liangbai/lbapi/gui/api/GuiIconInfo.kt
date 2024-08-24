package site.liangbai.lbapi.gui.api

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.translate.TransType
import site.liangbai.lbapi.translate.Translator
import site.liangbai.lbapi.translate.Translator.applyTranslate
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem

data class GuiIconInfo(val material: Material, val modelData: Int, val name: String, val lore: List<String>, val xMaterial: XMaterial? = null) {
    private val nameTranslators = mutableListOf<TransType>()
    private val loreTranslators = mutableListOf<TransType>()

    init {
        init()
    }

    fun transToItem(useLore: Boolean = true, player: Player? = null, obj: Any? = null, builder: ItemBuilder.(GuiIconInfo) -> Unit): ItemStack {
        val func: ItemBuilder.() -> Unit = {
            name = nameTranslators.applyTranslate(this@GuiIconInfo.name, obj, player)
            if (modelData >= 0) {
                customModelData = modelData
            }
            if (useLore) {
                lore.addAll(this@GuiIconInfo.lore.map { loreTranslators.applyTranslate(it, obj, player) })
            }

            colored()
            builder(this@GuiIconInfo)
        }

        if (xMaterial != null) {
            return buildItem(xMaterial) {
                func(this)
            }
        }

        return buildItem(material) {
            func(this)
        }
    }

    private fun init() {
        val translators = Translator.getDefaultTransTypes().toTypedArray()
        withNameTranslator(*translators).withLoreTranslator(*translators)
    }

    fun withPlaceholder() = this.apply { withTranslator(TransType.PLACEHOLDER) }

    fun reset() = this.apply { nameTranslators.clear(); loreTranslators.clear(); init() }

    fun resetWithoutDefault() = this.apply { nameTranslators.clear(); loreTranslators.clear() }

    fun withNameTranslator(vararg translator: TransType) = this.apply { nameTranslators.addAll(translator) }

    fun withLoreTranslator(vararg translator: TransType) = this.apply { loreTranslators.addAll(translator) }

    fun withTranslator(vararg translator: TransType) = this.apply { withNameTranslator(*translator).withLoreTranslator(*translator) }
}