package site.liangbai.lbapi.serverbridge

import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import site.liangbai.lbapi.serverbridge.proxy.PlatformProxy
import site.liangbai.lbapi.serverbridge.proxy.impl.BukkitProxy
import taboolib.expansion.AlkaidRedis
import taboolib.expansion.SingleRedisConnection
import kotlin.reflect.KClass

object BridgeRegistry {
    private val registeredPacketClass = mutableMapOf<Class<*>, MutableList<Processor>>()

    lateinit var proxy: PlatformProxy
    lateinit var redis: SingleRedisConnection
    lateinit var redisConfig: RedisConfig

    fun initialize(identity: String, redisConfig: RedisConfig) {
        this.redisConfig = redisConfig
        redis = AlkaidRedis.create()
            .host(redisConfig.host)
            .port(redisConfig.port)
            .auth(redisConfig.auth)
            .pass(redisConfig.password)
            .connect()
            .connection()
        proxy = BukkitProxy()
        proxy.registerChannel(identity)
    }

    fun isRedisCreated() = ::redis.isInitialized

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