package site.liangbai.lbapi.nbt.pokemon

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon
import net.minecraft.nbt.NBTTagCompound
import taboolib.module.nms.ItemTag
import taboolib.module.nms.NMSItemTag

object PokemonProvider {
    fun Pokemon.getPokemonTag(): ItemTag {
        return NMSItemTag.instance.itemTagToBukkitCopy(writeToNBT(NBTTagCompound())).asCompound()
    }

    fun Pokemon.setPokemonTag(tag: ItemTag): Pokemon {
        readFromNBT(NMSItemTag.instance.itemTagToNMSCopy(tag) as NBTTagCompound)
        return this
    }
}