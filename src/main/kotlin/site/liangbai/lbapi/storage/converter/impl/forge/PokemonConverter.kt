package site.liangbai.lbapi.storage.converter.impl.forge

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.pixelmonmod.pixelmon.Pixelmon
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon
import com.pixelmonmod.pixelmon.enums.EnumSpecies
import site.liangbai.lbapi.storage.converter.IConverter
import site.liangbai.lbapi.nbt.pokemon.PokemonProvider.getPokemonTag
import site.liangbai.lbapi.nbt.pokemon.PokemonProvider.setPokemonTag
import site.liangbai.lbapi.util.toItemTag
import site.liangbai.lbapi.util.toJsonElement

class PokemonConverter : IConverter<Pokemon> {
    override fun convertToElement(value: Pokemon): JsonElement {
        return JsonObject().apply {
            addProperty("species", value.species.name)
            add("nbt", value.getPokemonTag().toJsonElement())
        }
    }

    override fun convertFromString(data: JsonElement): Pokemon {
        val js = data.asJsonObject
        val species = EnumSpecies.valueOf(js["species"].asString)

        return Pixelmon.pokemonFactory.create(species).apply {
            setPokemonTag(js["nbt"].toItemTag())
        }
    }
}