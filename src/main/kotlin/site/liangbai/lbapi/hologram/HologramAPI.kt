package site.liangbai.lbapi.hologram

import org.bukkit.Location
import site.liangbai.lbapi.hologram.api.HologramBuilder
import site.liangbai.lbapi.hologram.api.HologramComponent
import site.liangbai.lbapi.hologram.api.TextHologram

/**
 * @author Arasple
 * @date 2021/2/10 9:38
 */
object HologramAPI {

    /**
     * 实体 ID 取得
     */
    private var INDEX = resetIndex()

    internal fun getIndex(): Int {
        return INDEX++
    }

    internal fun resetIndex(): Int {
        return 1197897763 + (0..7763).random()
    }

    @JvmStatic
    fun getHologramById(id: String): Hologram? {
        return Hologram.holograms.find { it.id == id }
    }

    @JvmStatic
    fun createTextCompoent(
        initText: String,
        location: Location,
        tick: Long = -1,
        onTick: (HologramComponent) -> Unit = {}
    ): TextHologram {
        return TextHologram(initText, Position.fromLocation(location), tick, onTick)
    }

    @JvmStatic
    fun builder(location: Location): HologramBuilder {
        return HologramBuilder(location = location)
    }
}