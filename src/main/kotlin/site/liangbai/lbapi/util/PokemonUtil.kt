package site.liangbai.lbapi.util

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.gui.api.GuiIconInfo
import site.liangbai.lbapi.nms.NMS
import site.liangbai.lbapi.text.translate.TransType

fun GuiIconInfo.withPokemonTranslator(): GuiIconInfo {
    return apply { withTranslator(TransType.PIXELMON) }
}

fun GuiIconInfo.withPokemonItem(pokemon: Pokemon): GuiIconInfo {
    return apply { this.customItem = pokemon.getPhotoItem() }
}

fun Pokemon.getPhotoItem(): ItemStack {
    return NMS.INSTANCE.toBukkitItem(ItemPixelmonSprite.getPhoto(this))
}