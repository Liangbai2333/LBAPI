package site.liangbai.lbapi.util

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.config.mapper.type.GuiIconInfo
import site.liangbai.lbapi.nms.NMS
import site.liangbai.lbapi.text.translate.TransType

fun GuiIconInfo.withPokemonTranslator(): GuiIconInfo {
    return apply { withTranslator(TransType.PIXELMON) }
}

fun GuiIconInfo.withPokemonItem(pokemon: Pokemon): GuiIconInfo {
    return apply { this.customItem = pokemon.getPhotoItem() }
}

fun Pokemon.getPhotoItem(): ItemStack {
    return NMS.INSTANCE.itemAsBukkitCopy(ItemPixelmonSprite.getPhoto(this))
}

fun Pokemon.setLevelOriginal(level: Int) {
    levelContainer.level = level
    this.experience = 0
}

fun EVStore.getPercentage(decimalPlaces: Int): Double {
    val percentage = total.toDouble() / 510.0 * 100.0
    return percentage.truncateWithDecimalPlaces(decimalPlaces)
}

fun EVStore.getPercentageString(decimalPlaces: Int): String {
    return getPercentage(decimalPlaces).getFormatString(decimalPlaces)
}

