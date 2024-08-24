package site.liangbai.lbapi.nbt.entity

import org.bukkit.entity.Entity
import site.liangbai.lbapi.nms.NMS
import site.liangbai.lbapi.util.getMethodSilently
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.ItemTag
import taboolib.module.nms.NMSItemTag

// 全反射 TODO NMS
object EntityProvider {
    fun Entity.getEntityTag(): ItemTag {
        val handle = this.getProperty<Any>("entity")!!
        val ref = ReflexClass.of(handle.javaClass)
        val method = ref.getMethodSilently("writeToNBT", NMS.INSTANCE.getNBTClass()) ?:
        ref.getMethodSilently("func_189511_e", NMS.INSTANCE.getNBTClass()) ?:
        ref.getMethodSilently("save", NMS.INSTANCE.getNBTClass()) ?: throw NoSuchMethodException("read nbt")
        val nbt = method.invoke(handle, NMS.INSTANCE.getNBTClass().invokeConstructor())!!

        return NMSItemTag.instance.itemTagToBukkitCopy(nbt).asCompound()
    }

    fun Entity.setEntityTag(tag: ItemTag): Entity {
        val handle = this.getProperty<Any>("entity")!!
        val ref = ReflexClass.of(handle.javaClass)
        val method = ref.getMethodSilently("readFromNBT", NMS.INSTANCE.getNBTClass()) ?:
        ref.getMethodSilently("load", NMS.INSTANCE.getNBTClass()) ?:
        ref.getMethodSilently("f", NMS.INSTANCE.getNBTClass()) ?: throw NoSuchMethodException("write nbt")

        method.invoke(handle, NMSItemTag.instance.itemTagToNMSCopy(tag))
        return this
    }
}