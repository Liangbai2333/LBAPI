package site.liangbai.lbapi.serverbridge.proxy.impl

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.messaging.PluginMessageListener
import site.liangbai.lbapi.serverbridge.BridgeRegistry
import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import site.liangbai.lbapi.serverbridge.packet.request.PlayerEmptyPacket
import site.liangbai.lbapi.serverbridge.packet.request.PostProcessPacket
import site.liangbai.lbapi.serverbridge.packet.request.RecoveryPacket
import site.liangbai.lbapi.serverbridge.packet.request.RegisterPacket
import site.liangbai.lbapi.serverbridge.packet.server.AllowNextPacket
import site.liangbai.lbapi.serverbridge.proxy.PlatformProxy
import site.liangbai.lbapi.serverbridge.util.transToByteArray
import site.liangbai.lbapi.serverbridge.util.transToPacket
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.function.registerBukkitListener
import taboolib.platform.BukkitPlugin
import java.util.*
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

    private var waitedRecovered = false

    private val uniqueId by lazy { UUID.randomUUID().toString() }

    private val threadPool = Executors.newSingleThreadExecutor()

    override fun registerChannel(identity: String) {
        incoming = "$identity:server"
        outgoing = "$identity:proxy"

        Bukkit.getMessenger().registerOutgoingPluginChannel(BukkitPlugin.getInstance(), outgoing)
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitPlugin.getInstance(), incoming, this)

        registered = true
        registerBukkitListener(PlayerJoinEvent::class.java, EventPriority.HIGHEST) {
            if (registered && !initialized) {
                sendPrivatePacket(it.player, RegisterPacket(uniqueId))
                initialized = true
            } else if (waitedRecovered) {
                sendPrivatePacket(it.player, RecoveryPacket())
                waitedRecovered = false
            }
        }
        registerBukkitListener(PlayerQuitEvent::class.java, EventPriority.HIGHEST) {
            if (Bukkit.getOnlinePlayers().size <= 1) {
                sendPrivatePacket(it.player, PlayerEmptyPacket())
                waitedRecovered = true
            }
        }
    }

    override fun sendPacket(packet: PluginPacket) {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return
        }

        lock.lock()
        try {
            threadPool.submit {
                while (!isAllowedToSend) {
                    canSend.await()
                }

                packet.uniqueId = uniqueId
                Bukkit.getOnlinePlayers().firstOrNull()?.sendPluginMessage(BukkitPlugin.getInstance(), outgoing, packet.transToByteArray())

                isAllowedToSend = false
            }

        } finally {
            lock.unlock()
        }
    }

    // 不需要服务器返回的包
    private fun sendPrivatePacket(player: Player, packet: PluginPacket) {
        packet.uniqueId = uniqueId
        player.sendPluginMessage(BukkitPlugin.getInstance(), outgoing, packet.transToByteArray())
    }

    override fun onPluginMessageReceived(channel: String, p: Player?, data: ByteArray) {
        if (channel == incoming) {
            lock.lock()
            try {
                val packet = data.transToPacket<PluginPacket>()

                if (packet.uniqueId != uniqueId) {
                    return
                }

                if (packet is AllowNextPacket) {
                    isAllowedToSend = true
                    canSend.signal()
                }

                val cls = packet.javaClass
                BridgeRegistry.getProcessors(cls)
                    .forEach { it.func(packet) }

                if (Bukkit.getOnlinePlayers().isNotEmpty()) {
                    sendPrivatePacket(Bukkit.getOnlinePlayers().first(), PostProcessPacket())
                }
            } finally {
                lock.unlock()
            }
        }
    }
}