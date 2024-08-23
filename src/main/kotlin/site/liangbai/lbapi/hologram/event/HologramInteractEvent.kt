package site.liangbai.lbapi.hologram.event

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import site.liangbai.lbapi.hologram.Hologram

/**
 * @author Arasple
 * @date 2021/2/11 16:34
 */
val staticHandlerList = HandlerList()

class HologramInteractEvent(val player: Player, val type: Type, val hologram: Hologram) : Event(!Bukkit.isPrimaryThread()), Cancellable {
    var cancel = false

    enum class Type {

        ALL,

        LEFT,

        RIGHT,

        SHIFT_LEFT,

        SHIFT_RIGHT

    }

    override fun getHandlers(): HandlerList {
        return staticHandlerList
    }

    fun getHandlerList(): HandlerList {
        return staticHandlerList
    }

    override fun isCancelled(): Boolean {
        return cancel
    }

    override fun setCancelled(c: Boolean) {
        cancel = c
    }

}