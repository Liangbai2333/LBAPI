package site.liangbai.lbapi.util

import com.google.gson.JsonElement
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagSerializer

fun ItemTag.toJsonElement(): JsonElement {
    return ItemTagSerializer.serializeData(this)
}

fun JsonElement.toItemTag(): ItemTag {
    return ItemTagSerializer.deserializeData(this).asCompound()
}

fun ItemTagData.asBoolean(): Boolean {
    return this.asByte() != 0.toByte()
}

fun booleanItemTagData(value: Boolean): ItemTagData {
    return ItemTagData(if (value) 1.toByte() else 0.toByte())
}