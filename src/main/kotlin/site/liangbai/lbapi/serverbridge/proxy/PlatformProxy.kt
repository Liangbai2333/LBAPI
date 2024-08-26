package site.liangbai.lbapi.serverbridge.proxy

import site.liangbai.lbapi.serverbridge.packet.PluginPacket

interface PlatformProxy {
    fun registerChannel(identity: String)

    fun sendPacket(packet: PluginPacket)
}