package site.liangbai.lbapi.serverbridge.util

import site.liangbai.lbapi.serverbridge.BridgeRegistry
import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import java.util.LinkedList
import java.util.concurrent.locks.Lock

fun PluginPacket.send() {
    BridgeRegistry.proxy.sendPackets(LinkedList<PluginPacket>().apply { add(this@send) })
}

fun List<PluginPacket>.send() {
    BridgeRegistry.proxy.sendPackets(this)
}

fun Array<PluginPacket>.send() {
    BridgeRegistry.proxy.sendPackets(this.toList())
}

fun Collection<PluginPacket>.send() {
    BridgeRegistry.proxy.sendPackets(this.toList())
}

fun Lock.withLock(action: () -> Unit) {
    try {
        lock()
        action()
    } finally {
        unlock()
    }
}