package site.liangbai.lbapi.hologram.base

import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author Arasple
 * @date 2021/2/12 14:08
 */
fun interface BaseCondition {

    fun eval(player: Player): CompletableFuture<Boolean>

}