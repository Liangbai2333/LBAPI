package site.liangbai.lbapi.hologram.nms.packet

import site.liangbai.lbapi.hologram.nms.NMS
import org.bukkit.entity.Player
import java.util.*

/**
 * @author Arasple
 * @date 2020/12/4 21:22
 */
abstract class PacketEntity(val entityId: Int = -1, val uuid: UUID? = null) {

    fun send(player: Player) = NMS.INSTANCE.sendEntityPacket(player, this)

}