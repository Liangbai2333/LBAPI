package site.liangbai.lbapi.hologram.api

import org.bukkit.Location
import site.liangbai.lbapi.hologram.Hologram
import site.liangbai.lbapi.hologram.Position
import site.liangbai.lbapi.hologram.base.BaseCondition
import site.liangbai.lbapi.hologram.base.ClickHandler
import site.liangbai.lbapi.hologram.base.TickEvent
import java.util.*

/**
 * @author Arasple
 * @date 2021/2/12 14:04
 */
class HologramBuilder(
    private val id: String = UUID.randomUUID().toString(),
    private val location: Location,
    var offset: Double = 0.25,
    var viewDistance: Double = 15.0,
    var viewCondition: BaseCondition? = null,
    var refreshCondition: Long = -1,
    var clickHandler: ClickHandler = ClickHandler { _, _ -> }
) {

    private val components = mutableListOf<HologramComponent>()
    private var position = Position.fromLocation(location).clone(y = -1.25)

    fun offset(value: Double): HologramBuilder {
        offset = value
        return this
    }

    fun interspace(value: Double): HologramBuilder {
        position = position.clone(y = -value)
        return this
    }

    /**
     * Append a text line
     */
    @JvmOverloads
    fun append(
        text: String,
        update: Long = -1,
        offset: Double = this.offset,
        onTick: TickEvent? = null
    ): HologramBuilder {
        interspace(offset)
        components.add(TextHologram(text, position, update, onTick))
        return this
    }

    fun viewDistance(value: Double): HologramBuilder {
        viewDistance = value
        return this
    }

    fun viewCondition(value: BaseCondition): HologramBuilder {
        viewCondition = value
        return this
    }

    fun refreshCondition(period: Long): HologramBuilder {
        refreshCondition = period
        return this
    }

    fun click(handler: ClickHandler): HologramBuilder {
        clickHandler = handler
        return this
    }

    fun removeLast() {
        components.removeLast()
        position = components.last().position
    }

    /**
     * @param external
     */
    @JvmOverloads
    fun build(external: Boolean = true): Hologram {
        return Hologram(
            id,
            Position.fromLocation(location),
            viewDistance,
            viewCondition,
            refreshCondition,
            components,
            clickHandler
        ).also {
            (if (external) Hologram.externalHolograms
            else Hologram.holograms).add(it)
        }
    }

}