package site.liangbai.lbapi.serverbridge.util

import site.liangbai.lbapi.serverbridge.BridgeRegistry
import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToString
import java.util.concurrent.locks.Lock

fun PluginPacket.transToByteArray(): ByteArray {
    val data = this.convertToString()
    return data.toByteArray(Charsets.UTF_8)
}

fun <T : PluginPacket> ByteArray.transToPacket(): T {
    return String(this, Charsets.UTF_8).convertToEntity()
}

fun PluginPacket.send() {
    BridgeRegistry.proxy.sendPacket(this)
}

fun Lock.withLock(action: () -> Unit) {
    try {
        lock()
        action()
    } finally {
        unlock()
    }
}