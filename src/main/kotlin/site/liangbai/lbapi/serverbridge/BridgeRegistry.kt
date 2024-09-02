package site.liangbai.lbapi.serverbridge

import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import site.liangbai.lbapi.serverbridge.proxy.PlatformProxy
import site.liangbai.lbapi.serverbridge.proxy.impl.BukkitProxy
import site.liangbai.lbapi.serverbridge.proxy.impl.BungeeProxy
import taboolib.common.platform.Platform
import taboolib.common.platform.function.runningPlatform
import kotlin.reflect.KClass

object BridgeRegistry {
    private val registeredPacketClass = mutableMapOf<Class<*>, MutableList<Processor>>()

    lateinit var proxy: PlatformProxy

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

    @Suppress("UNCHECKED_CAST")
    fun <T : PluginPacket> registerPacket(packetClass: KClass<T>, processor: (T) -> Unit) {
        if (packetClass.java !in registeredPacketClass) {
            registeredPacketClass[packetClass.java] = mutableListOf()
        }

        registeredPacketClass[packetClass.java]!!.add(Processor(processor as (PluginPacket) -> Unit))
    }

    fun getProcessors(packetType: Class<*>): List<Processor> {
        return registeredPacketClass[packetType] ?: emptyList()
    }

    class Processor(val func: (PluginPacket) -> Unit)
}