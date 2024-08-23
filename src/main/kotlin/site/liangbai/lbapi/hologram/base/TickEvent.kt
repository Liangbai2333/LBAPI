package site.liangbai.lbapi.hologram.base

import site.liangbai.lbapi.hologram.api.HologramComponent


/**
 * @author Arasple
 * @date 2021/2/12 20:38
 */
fun interface TickEvent {

    fun run(hologramComponent: HologramComponent)

}