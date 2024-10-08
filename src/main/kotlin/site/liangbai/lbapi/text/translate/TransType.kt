package site.liangbai.lbapi.text.translate

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import site.liangbai.lbapi.util.getPercentageString

class TransType(val default: Boolean, val translator: (Any?, String, Player?) -> String) {
    companion object {
        val PIXELMON = TransType(false) { pokemon, original, _ ->
            if (pokemon !is Pokemon) {
                original
            } else {
                val ivs = pokemon.iVs
                val evs = pokemon.eVs

                original.replace("%pokemon_shiny%", if (pokemon.isShiny) "是" else "否")
                    .replace("%pokemon_gender%", pokemon.gender.localizedName)
                    .replace("%pokemon_ability%", pokemon.ability.localizedName)
                    .replace("%pokemon_nature%", pokemon.nature.localizedName)
                    .replace("%ivs_hp%", ivs.getStat(StatsType.HP).toString())
                    .replace("%ivs_defence%", ivs.getStat(StatsType.Defence).toString())
                    .replace("%ivs_attack%", ivs.getStat(StatsType.Attack).toString())
                    .replace("%ivs_speed%", ivs.getStat(StatsType.Speed).toString())
                    .replace("%ivs_special_defence%", ivs.getStat(StatsType.SpecialDefence).toString())
                    .replace("%ivs_special_attack%", ivs.getStat(StatsType.SpecialAttack).toString())
                    .replace("%ivs_percent%", ivs.getPercentageString(0))
                    .replace("%evs_hp%", evs.getStat(StatsType.HP).toString())
                    .replace("%evs_defence%", evs.getStat(StatsType.Defence).toString())
                    .replace("%evs_attack%", evs.getStat(StatsType.Attack).toString())
                    .replace("%evs_speed%", evs.getStat(StatsType.Speed).toString())
                    .replace("%evs_special_defence%", evs.getStat(StatsType.SpecialDefence).toString())
                    .replace("%evs_special_attack%", evs.getStat(StatsType.SpecialAttack).toString())
                    .replace("%evs_percent%", evs.getPercentageString(0))
                    .replace("%pokemon_name%", pokemon.localizedName)
                    .replace("%pokemon_level%", pokemon.level.toString())
            }
        }
        val PLAYER_INFO = TransType(true) { player1, original, player2 ->
            var player: Player? = null
            if (player2 is Player) {
                player = player2
            } else if (player1 is Player) {
                player = player1
            }
            if (player != null) {
                original.replace("%player_name%", player.name)
                    .replace("%player_level%", player.level.toString())
                    .replace("%player_health%", player.health.toString())
                    .replace("%player_uuid%", player.uniqueId.toString())
            } else {
                original
            }
        }
        val PLACEHOLDER = TransType(false) { _, text, player ->
            if (player == null) {
                text
            } else {
                PlaceholderAPI.setPlaceholders(player, text)
            }
        }

        private val registeredTranslators = mutableListOf<TransType>()
        private val customTranslators = mutableMapOf<String, TransType>()

        fun registerCustomTranslator(identity: String, default: Boolean, translator: (Any?, String, Player?) -> String) {
            val transType = TransType(default, translator)

            if (default) {
                registeredTranslators.add(transType)
            }

            customTranslators[identity] = transType
        }

        fun getCustomTranslator(identity: String) = customTranslators[identity]

        fun defaultValues(): List<TransType> {
            if (registeredTranslators.isEmpty()) {
                registeredTranslators.add(PIXELMON)
                registeredTranslators.add(PLACEHOLDER)
                registeredTranslators.add(PLAYER_INFO)
            }

            return registeredTranslators.filter { it.default }.toList()
        }
    }
}