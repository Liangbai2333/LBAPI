package site.liangbai.lbapi.hologram.base

import org.bukkit.entity.Player
import site.liangbai.lbapi.hologram.event.HologramInteractEvent

/**
 * @author Arasple
 * @date 2021/2/12 14:10
 */
fun interface ClickHandler {

    fun eval(player: Player, type: HologramInteractEvent.Type)

}