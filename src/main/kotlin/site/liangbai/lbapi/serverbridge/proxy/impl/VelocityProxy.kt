package site.liangbai.lbapi.serverbridge.proxy.impl

import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.ChannelMessageSource
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
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
import taboolib.common.platform.function.registerVelocityListener
import taboolib.platform.VelocityPlugin
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock

@PlatformSide(Platform.VELOCITY)
class VelocityProxy : PlatformProxy {
    private lateinit var incoming: MinecraftChannelIdentifier
    private lateinit var outgoing: MinecraftChannelIdentifier

    private val lock = ReentrantLock()
    private val canSend = lock.newCondition()
    private var isAllowedToSend = true

    private val registeredServerName = mutableListOf<String>()
    private val postCache = mutableListOf<String>()
    private var emptyServerCount = 0

    private val threadPool = Executors.newSingleThreadExecutor()

    private val plugin by lazy { VelocityPlugin.getInstance() }

    override fun registerChannel(identity: String) {
        incoming = MinecraftChannelIdentifier.from("$identity:proxy")
        outgoing = MinecraftChannelIdentifier.from("$identity:server")

        plugin.server.channelRegistrar.register(incoming, outgoing)

        registerVelocityListener(PluginMessageEvent::class.java) {
            lock.withLock {
                if (it.identifier == incoming) {
                    val packet = it.data.transToPacket<PluginPacket>()
                    if (packet is RegisterPacket) {
                        val server = it.source.toServer()
                        val senderInfo = server.serverInfo
                        val sender = senderInfo.name
                        if (sender !in registeredServerName) {
                            registeredServerName.add(sender)
                            sendPrivatePacket(server, RegisteredPacket())
                        }
                        return@withLock
                    }
                    if (packet is PostProcessPacket) {
                        val server = it.source.toServer()
                        val senderInfo = server.serverInfo
                        val sender = senderInfo.name
                        postCache.add(sender)

                        if (postCache.size >= (registeredServerName.size - emptyServerCount)) {
                            sendPacket(AllowNextPacket())
                            postCache.clear()
                            isAllowedToSend = true
                            canSend.signal()
                        }
                        return@withLock
                    }
                    processPacket(packet)
                }
            }
        }

        registerVelocityListener(KickedFromServerEvent::class.java) {
            val server = it.server
            if (server.serverInfo.name in registeredServerName && server.playersConnected.isEmpty()) {
                emptyServerCount += 1
            }
        }

        registerVelocityListener(ServerConnectedEvent::class.java) {
            val server = it.server
            if (server.serverInfo.name in registeredServerName && server.playersConnected.isEmpty()) {
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
            plugin.server.getServer(it).get().sendPluginMessage(outgoing, packet.transToByteArray())
        }
    }

    private fun sendPrivatePacket(server: RegisteredServer, packet: PluginPacket) {
        server.sendPluginMessage(outgoing, packet.transToByteArray())
    }

    private fun ChannelMessageSource.toServer(): RegisteredServer {
        return when (this) {
            is ServerConnection -> this.server
            is Player -> this.currentServer.get().server
            else -> throw IllegalArgumentException()
        }
    }
}