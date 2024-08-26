package site.liangbai.lbapi.serverbridge.util

import site.liangbai.lbapi.serverbridge.packet.PluginPacket
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToEntity
import site.liangbai.lbapi.storage.converter.ConverterManager.convertToString

fun PluginPacket.transToByteArray(): ByteArray {
    val data = this.convertToString()
    return data.toByteArray(Charsets.UTF_8)
}

fun <T : PluginPacket> ByteArray.transToPacket(): T {
    return String(this, Charsets.UTF_8).convertToEntity()
}