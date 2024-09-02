package site.liangbai.lbapi.serverbridge.proxy.impl

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PluginMessageEvent
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

    private val registeredServer = mutableListOf<String>()
    private val postCache = mutableListOf<String>()
    private val playerEmptyServer = mutableListOf<String>()

    private val threadPool = Executors.newSingleThreadExecutor()

    override fun registerChannel(identity: String) {
        incoming = "$identity:proxy"
        outgoing = "$identity:server"

        server<ProxyServer>().registerChannel(incoming)
        server<ProxyServer>().registerChannel(outgoing)

        registerBungeeListener(PluginMessageEvent::class.java) {
            try {
                lock.lock()
                if (it.tag == incoming) {
                    val packet = it.data.transToPacket<PluginPacket>()
                    if (packet is RegisterPacket) {
                        registeredServer.add(packet.registeredUniqueId)
                        return@registerBungeeListener
                    }
                    if (packet is PlayerEmptyPacket) {
                        playerEmptyServer.add(packet.uniqueId)
                        return@registerBungeeListener
                    }
                    if (packet is RecoveryPacket) {
                        playerEmptyServer.remove(packet.uniqueId)
                        return@registerBungeeListener
                    }
                    if (packet is PostProcessPacket) {
                        postCache.add(packet.uniqueId)

                        if (postCache.size >= (registeredServer.size - playerEmptyServer.size)) {
                            registeredServer.forEach {
                                sendPacket(AllowNextPacket().apply { uniqueId = it })
                            }
                            postCache.clear()
                            isAllowedToSend = true
                            canSend.signal()
                        }
                        return@registerBungeeListener
                    }
                    processPacket(packet)
                }
            } finally {
                lock.unlock()
            }
        }
    }

    fun processPacket(packet: PluginPacket) {
        threadPool.submit {
            try {
                lock.lock()
                while (!isAllowedToSend) {
                    canSend.await()
                }

                registeredServer.forEach {
                    sendPacket(packet.apply { uniqueId = it })
                }

                isAllowedToSend = false
            } finally {
                lock.unlock()
            }
        }
    }

    override fun sendPacket(packet: PluginPacket) {
        server<ProxyServer>().servers.forEach { (_, u) ->
            u.sendData(outgoing, packet.transToByteArray())
        }
    }
}