package site.liangbai.lbapi.hologram.nms

import com.mojang.authlib.GameProfile
import site.liangbai.lbapi.hologram.nms.packet.PacketEntity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common.util.unsafeLazy
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.nms.nmsProxy
import taboolib.module.nms.sendPacket

/**
 * @author Arasple
 * @date 2020/12/4 21:20
 */
abstract class NMS {

    companion object {

        /**
         * @see NMSImpl
         */
        val INSTANCE by unsafeLazy {
            nmsProxy<NMS>()
        }

    }

    abstract fun sendEntityPacket(player: Player, vararg packets: PacketEntity)

    abstract fun sendEntityMetadata(player: Player, entityId: Int, vararg objects: Any)

    abstract fun parseVec3d(obj: Any): Vector

    fun sendPacket(player: Player, packet: Any, vararg fields: Pair<Any, Any>) {
        player.sendPacket(packet.also { inst ->
            fields.forEach { inst.setProperty(it.first.toString(), it.second) }
        })
    }

    abstract fun getGameProfile(player: Player): GameProfile

}