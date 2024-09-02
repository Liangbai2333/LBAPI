package site.liangbai.lbapi.config.mapper.gui.api

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.text.translate.TransType
import site.liangbai.lbapi.text.translate.Translator
import site.liangbai.lbapi.text.translate.Translator.applyTranslate
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem

data class GuiIconInfo(var material: Material, var modelData: Int, var name: String, var lore: List<String>, var damage: Int, var amount: Int, var xMaterial: XMaterial? = null, var customItem: ItemStack? = null) {
    private val nameTranslators = mutableSetOf<TransType>()
    private val loreTranslators = mutableSetOf<TransType>()

    init {
        init()
    }

    fun transToItem(useLore: Boolean = true, player: Player? = null, obj: Any? = null, builder: ItemBuilder.(GuiIconInfo) -> Unit): ItemStack {
        val func: ItemBuilder.() -> Unit = {
            name = nameTranslators.applyTranslate(this@GuiIconInfo.name, obj, player)
            if (modelData >= 0) {
                customModelData = modelData
            }
            if (damage > 0) {
                this.damage = this@GuiIconInfo.damage
            }
            if (amount > 1) {
                this.amount = this@GuiIconInfo.amount
            }
            if (useLore) {
                lore.addAll(this@GuiIconInfo.lore.map { loreTranslators.applyTranslate(it, obj, player) })
            }

            colored()
            builder(this@GuiIconInfo)
        }

        if (customItem != null) {
            return buildItem(customItem!!) {
                func(this)
            }
        }

        if (xMaterial != null) {
            return buildItem(xMaterial!!) {
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