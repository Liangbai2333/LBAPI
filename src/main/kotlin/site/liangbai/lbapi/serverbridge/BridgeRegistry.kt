package site.liangbai.lbapi.serverbridge

import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import site.liangbai.lbapi.serverbridge.proxy.PlatformProxy
import site.liangbai.lbapi.serverbridge.proxy.impl.BukkitProxy
import site.liangbai.lbapi.serverbridge.proxy.impl.BungeeProxy
import taboolib.common.platform.Platform
import taboolib.common.platform.function.runningPlatform

object BridgeRegistry {
    private val registeredPacketClass = mutableMapOf<Class<*>, MutableList<Processor>>()

    private lateinit var proxy: PlatformProxy

    fun initialize(identity: String) {
        proxy = when (runningPlatform) {
            Platform.BUKKIT -> BukkitProxy()
            Platform.BUNGEE -> BungeeProxy()
            Platform.VELOCITY -> BungeeProxy()
            Platform.AFYBROKER -> throw IllegalArgumentException("unsupported platform")
            Platform.APPLICATION -> throw IllegalArgumentException("unsupported platform")
        }
        proxy.registerChannel(identity)
    }

    fun <T : PluginPacket> registerPacket(packetClass: Class<T>, processor: (PluginPacket) -> Unit) {
        if (packetClass !in registeredPacketClass) {
            registeredPacketClass[packetClass] = mutableListOf()
        }

        registeredPacketClass[packetClass]!!.add(Processor(processor))
    }

    fun getProcessors(packetType: Class<*>): List<Processor> {
        return registeredPacketClass[packetType] ?: emptyList()
    }

    class Processor(val func: (PluginPacket) -> Unit)
}