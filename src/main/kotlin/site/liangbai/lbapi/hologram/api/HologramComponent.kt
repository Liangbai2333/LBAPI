package site.liangbai.lbapi.hologram.api

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import site.liangbai.lbapi.hologram.Position
import site.liangbai.lbapi.hologram.HologramAPI
import site.liangbai.lbapi.hologram.base.TickEvent
import site.liangbai.lbapi.hologram.nms.packet.PacketEntityDestroy
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import kotlin.properties.Delegates

/**
 * @author Arasple
 * @date 2021/2/10 10:36
 */
abstract class HologramComponent(
    val position: Position,
    tick: Long = -1,
    val onTick: TickEvent?
) {

    abstract fun spawn(player: Player)

    abstract fun onTick()

    val entityId by lazy { HologramAPI.getIndex() }

    internal val viewers = mutableSetOf<String>()

    private var task: PlatformExecutor.PlatformTask? = null

    var period by Delegates.observable(tick) { _, _, _ ->
        deployment()
    }

    init {
        deployment()
    }

    private fun deployment() {
        task?.cancel()
        if (period > 0) task = submit(delay = period, period = period, async = true) {
            viewers.removeIf {
                val player = Bukkit.getPlayerExact(it)
                player == null || !player.isOnline
            }
            onTick()
            onTick?.run(this@HologramComponent)
        }
    }

    fun destroy(player: Player) {
        PacketEntityDestroy(entityId).send(player)
        viewers.remove(player.name)
    }

    fun destroy() {
        task?.cancel()
        forViewers { destroy(it) }
    }

    fun forViewers(viewer: (Player) -> Unit) {
        viewers.mapNotNull { Bukkit.getPlayerExact(it) }.forEach(viewer)
    }

}