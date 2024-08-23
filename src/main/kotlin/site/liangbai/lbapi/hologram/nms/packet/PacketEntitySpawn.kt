package site.liangbai.lbapi.hologram.nms.packet

import site.liangbai.lbapi.hologram.Position

/**
 * @author Arasple
 * @date 2021/1/25 12:20
 */
class PacketEntitySpawn(entityId: Int, val position: Position, val type: Boolean = true) : PacketEntity(entityId)