package site.liangbai.lbapi.hologram.api

import org.bukkit.entity.Player
import site.liangbai.lbapi.hologram.Position
import site.liangbai.lbapi.hologram.base.TickEvent
import site.liangbai.lbapi.hologram.nms.packet.PacketArmorStandModify
import site.liangbai.lbapi.hologram.nms.packet.PacketArmorStandName
import site.liangbai.lbapi.hologram.nms.packet.PacketEntitySpawn

/**
 * @author Arasple
 * @date 2021/2/10 10:28
 *
 * Y: rawY + 1 ~ rawY + 1.25
 */
class TextHologram(
    name: String,
    position: Position,
    tick: Long,
    onTick: TickEvent? = null
) : HologramComponent(position, tick, onTick) {

    var text: String = name
        set(value) {
            onTick()
            field = value
        }

    private fun updateName(player: Player) {
        PacketArmorStandName(entityId, true, text).send(player)
    }

    override fun spawn(player: Player) {
        PacketEntitySpawn(entityId, position).send(player)
        PacketArmorStandModify(
            entityId,
            isInvisible = true,
            isGlowing = false,
            isSmall = true,
            hasArms = false,
            noBasePlate = true,
            isMarker = false
        ).send(player)
        updateName(player)

        viewers.add(player.name)
    }

    override fun onTick() {
        forViewers { updateName(it) }
    }

}