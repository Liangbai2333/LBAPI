package site.liangbai.lbapi.util

import com.google.gson.JsonElement
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagSerializer

fun ItemTag.toJsonElement(): JsonElement {
    return ItemTagSerializer.serializeData(this)
}

fun JsonElement.toItemTag(): ItemTag {
    return ItemTagSerializer.deserializeData(this).asCompound()
}