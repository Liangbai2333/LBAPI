package site.liangbai.lbapi.serverbridge.proxy.impl

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.Connection
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerDisconnectEvent
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
import taboolib.common.platform.function.registerBungeeListener
import taboolib.common.platform.function.server
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock

@PlatformSide(Platform.BUNGEE)
class BungeeProxy : PlatformProxy {
    private lateinit var incoming: String
    private lateinit var outgoing: String

    private val lock = ReentrantLock()
    private val canSend = lock.newCondition()
    private var isAllowedToSend = true

    private val registeredServerName = mutableListOf<String>()
    private val postCache = mutableListOf<String>()
    private var emptyServerCount = 0

    private val threadPool = Executors.newSingleThreadExecutor()

    private val server by lazy { server<ProxyServer>() }

    override fun registerChannel(identity: String) {
        incoming = "$identity:proxy"
        outgoing = "$identity:server"

        server<ProxyServer>().registerChannel(incoming)
        server<ProxyServer>().registerChannel(outgoing)

        registerBungeeListener(PluginMessageEvent::class.java) {
            lock.withLock {
                if (it.tag == incoming) {
                    val packet = it.data.transToPacket<PluginPacket>()
                    if (packet is RegisterPacket) {
                        val serverInfo = it.sender.toServerInfo()
                        val sender = serverInfo.name
                        if (sender !in registeredServerName) {
                            registeredServerName.add(sender)
                            sendPrivatePacket(serverInfo, RegisteredPacket())
                        }
                        return@withLock
                    }
                    if (packet is PostProcessPacket) {
                        val sender = it.sender.toServerInfo().name
                        postCache.add(sender)

                        if (postCache.size >= (registeredServerName.size - emptyServerCount)) {
                            sendPacket(AllowNextPacket())
                            postCache.clear()
                            isAllowedToSend = true
                            canSend.signalAll()
                        }
                        return@withLock
                    }
                    processPacket(packet)
                }
            }
        }

        registerBungeeListener(ServerDisconnectEvent::class.java) {
            val serverInfo = it.target
            if (serverInfo.name in registeredServerName && serverInfo.players.isEmpty()) {
                emptyServerCount += 1
            }
        }

        registerBungeeListener(ServerConnectedEvent::class.java) {
            val serverInfo = it.server.info
            if (serverInfo.name in registeredServerName && serverInfo.players.isEmpty()) {
                emptyServerCount -= 1
            }
        }
    }

    private fun processPacket(packet: PluginPacket) {
        threadPool.submit {
            lock.withLock {
                while (!isAllowedToSend) {
                    canSend.await()
                }

                sendPacket(packet)

                isAllowedToSend = false
            }
        }
    }

    override fun sendPacket(packet: PluginPacket) {
        registeredServerName.forEach {
            server.getServerInfo(it).sendData(outgoing, packet.transToByteArray())
        }
    }

    private fun sendPrivatePacket(server: ServerInfo, packet: PluginPacket) {
        server.sendData(outgoing, packet.transToByteArray())
    }

    private fun Connection.toServerInfo(): ServerInfo {
        return when (this) {
            is Server -> this.info
            is ProxiedPlayer -> this.server.info
            else -> throw IllegalArgumentException()
        }
    }
}