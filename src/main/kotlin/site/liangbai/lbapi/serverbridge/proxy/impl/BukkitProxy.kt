package site.liangbai.lbapi.serverbridge.proxy.impl

import site.liangbai.lbapi.serverbridge.BridgeRegistry
import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import site.liangbai.lbapi.serverbridge.proxy.PlatformProxy
import site.liangbai.lbapi.serverbridge.util.withLock
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToString
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock

@PlatformSide(Platform.BUKKIT)
class BukkitProxy : PlatformProxy {
    private lateinit var channel: String

    private val lock = ReentrantLock()

    private val threadPool = Executors.newSingleThreadExecutor()

    override fun registerChannel(identity: String) {
        channel = "${BridgeRegistry.redisConfig.prefix}:$identity:server"

        BridgeRegistry.redis.subscribe(channel, patternMode = false) {
            lock.withLock {
                val list = this.message.convertToEntity<MutableList<PluginPacket>>()

                list.forEach { packet ->
                    val cls = packet.javaClass
                    BridgeRegistry.getProcessors(cls)
                        .forEach { it.func(packet) }
                }
            }
        }
    }

    override fun sendPackets(packets: List<PluginPacket>) {
        threadPool.submit {
            lock.withLock {
                if (packets.isEmpty()) {
                    return@withLock
                }
                BridgeRegistry.redis.publish(channel, packets.convertToString())
            }
        }
    }
}