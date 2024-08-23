package site.liangbai.lbapi.hologram.nms

import org.bukkit.Bukkit
import site.liangbai.lbapi.hologram.Hologram
import site.liangbai.lbapi.hologram.event.HologramInteractEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.PacketReceiveEvent

/**
 * @author Arasple
 * @date 2021/2/10 11:27
 */
object NMSListener {

    @SubscribeEvent
    fun useEntity(event: PacketReceiveEvent) {
        val packet = event.packet
        if (packet.name == "PacketPlayInUseEntity") {
            val entityId = packet.read<Int>("a")!!.also { if (it < 1197897763) return }
            val hologram =
                Hologram.findHologram { it -> it.components.any { it.entityId == entityId } } ?: return

            val sneaking = event.player.isSneaking
            val type = when (packet.read<String>("action")) {
                "ATTACK" -> if (sneaking) HologramInteractEvent.Type.SHIFT_LEFT else HologramInteractEvent.Type.LEFT
                else -> if (sneaking) HologramInteractEvent.Type.SHIFT_RIGHT else HologramInteractEvent.Type.RIGHT
            }

            Bukkit.getPluginManager().callEvent(HologramInteractEvent(event.player, type, hologram))
        }
    }
}