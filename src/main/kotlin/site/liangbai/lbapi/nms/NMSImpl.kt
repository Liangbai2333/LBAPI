package site.liangbai.lbapi.nms

import net.minecraft.server.v1_16_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftInventory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion

class NMSImpl : NMS() {
    override fun getTitleName(inventory: Inventory): String {
        return if (MinecraftVersion.versionId > 11300) {
            val craftInventory = inventory as CraftInventory

            val ref = ReflexClass
                .of(craftInventory.inventory.javaClass)
                .structure
            val getter = ref.getFieldSilently("title") ?: ref.getFieldSilently("name") ?: throw NullPointerException("title")

            getter.get(craftInventory.inventory) as String
        } else {
            inventory.title
        }
    }

    override fun getNBTClass(): Class<*> {
        return NBTTagCompound::class.java
    }

    override fun itemAsBukkitCopy(item: Any): ItemStack {
        return CraftItemStack.asBukkitCopy(item as net.minecraft.item.ItemStack)
    }
}