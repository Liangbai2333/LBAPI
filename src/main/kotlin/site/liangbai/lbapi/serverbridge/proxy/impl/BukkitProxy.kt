package site.liangbai.lbapi.serverbridge.proxy.impl

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.messaging.PluginMessageListener
import site.liangbai.lbapi.serverbridge.BridgeRegistry
import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import site.liangbai.lbapi.serverbridge.packet.request.PostProcessPacket
import site.liangbai.lbapi.serverbridge.packet.request.RegisterPacket
import site.liangbai.lbapi.serverbridge.packet.server.AllowNextPacket
import site.liangbai.lbapi.serverbridge.packet.server.RegisteredPacket
import site.liangbai.lbapi.serverbridge.proxy.PlatformProxy
import site.liangbai.lbapi.serverbridge.util.transToByteArray
import site.liangbai.lbapi.serverbridge.util.transToPacket
import site.liangbai.lbapi.serverbridge.util.withLock
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.function.registerBukkitListener
import taboolib.common.platform.function.submit
import taboolib.platform.BukkitPlugin
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock

@PlatformSide(Platform.BUKKIT)
class BukkitProxy : PlatformProxy, PluginMessageListener {
    private lateinit var incoming: String
    private lateinit var outgoing: String

    private val lock = ReentrantLock()
    private val canSend = lock.newCondition()
    private var isAllowedToSend = true

    private var initialized = false
    private var registered = false

    private val threadPool = Executors.newSingleThreadExecutor()

    override fun registerChannel(identity: String) {
        incoming = "$identity:server"
        outgoing = "$identity:proxy"

        Bukkit.getMessenger().registerOutgoingPluginChannel(BukkitPlugin.getInstance(), outgoing)
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitPlugin.getInstance(), incoming, this)

        registered = true
        registerBukkitListener(PlayerJoinEvent::class.java, EventPriority.HIGHEST) {
            if (registered && !initialized) {
                tryRegisterToBungee(it.player)
            }
        }
    }

    override fun sendPacket(packet: PluginPacket) {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return
        }

        threadPool.submit {
            lock.withLock {
                while (!isAllowedToSend) {
                    canSend.await()
                }

                Bukkit.getOnlinePlayers().firstOrNull()?.sendPluginMessage(BukkitPlugin.getInstance(), outgoing, packet.transToByteArray())

                isAllowedToSend = false
            }
        }
    }

    // 不需要服务器返回的包
    private fun sendPrivatePacket(player: Player, packet: PluginPacket) {
        player.sendPluginMessage(BukkitPlugin.getInstance(), outgoing, packet.transToByteArray())
    }

    private fun tryRegisterToBungee(player: Player) {
        submit(delay = 1, period = 2) {
            try {
                if (!player.isOnline || initialized) {
                    cancel()
                } else {
                    sendPrivatePacket(player, RegisterPacket())
                }
            } catch (_: Throwable) {
                cancel()
            }
        }
    }

    override fun onPluginMessageReceived(channel: String, p: Player?, data: ByteArray) {
        if (channel == incoming) {
            lock.withLock {
                val packet = data.transToPacket<PluginPacket>()

                if (packet is AllowNextPacket) {
                    isAllowedToSend = true
                    canSend.signalAll()
                    return@withLock
                }

                if (packet is RegisteredPacket) {
                    if (!initialized) initialized = true
                    return@withLock
                }

                val cls = packet.javaClass
                try {
                    BridgeRegistry.getProcessors(cls)
                        .forEach { it.func(packet) }
                } catch (t: Throwable) {
                    t.printStackTrace()
                } finally {
                    if (Bukkit.getOnlinePlayers().isNotEmpty()) {
                        sendPrivatePacket(Bukkit.getOnlinePlayers().first(), PostProcessPacket())
                    }
                }
            }
        }
    }
}