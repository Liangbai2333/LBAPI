package site.liangbai.lbapi.hologram.nms.packet

/**
 * @author Arasple
 * @date 2021/1/25 12:20
 */
class PacketArmorStandName(entityId: Int, val isCustomNameVisible: Boolean, val name: String) : PacketEntity(entityId)