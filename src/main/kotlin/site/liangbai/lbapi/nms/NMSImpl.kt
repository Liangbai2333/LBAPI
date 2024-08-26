package site.liangbai.lbapi.nms

import net.minecraft.item.ItemStack
import net.minecraft.server.v1_16_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftInventory
import org.bukkit.inventory.Inventory
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion

class NMSImpl : NMS() {
    override fun getTitleName(inventory: Inventory): String {
        return if (MinecraftVersion.majorLegacy > 11300) {
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

    override fun toBukkitItem(original: ItemStack): org.bukkit.inventory.ItemStack {
        return CraftItemStack.asBukkitCopy(original)
    }

    override fun toNMSItem(original: org.bukkit.inventory.ItemStack): ItemStack {
        return CraftItemStack.asNMSCopy(original)
    }
}