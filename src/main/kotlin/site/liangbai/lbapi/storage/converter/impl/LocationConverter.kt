package site.liangbai.lbapi.storage.converter.impl

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Location
import site.liangbai.lbapi.storage.converter.IConverter

class LocationConverter : IConverter<Location> {
    override fun convertToElement(value: Location): JsonElement {
        return JsonObject().apply {
            addProperty("world", value.world.name)
            addProperty("x", value.x)
            addProperty("y", value.y)
            addProperty("z", value.z)
            addProperty("yaw", value.yaw)
            addProperty("pitch", value.pitch)
        }
    }

    override fun convertFromString(data: JsonElement): Location {
        val js = data.asJsonObject
        return Location(
            Bukkit.getWorld(js["world"].asString),
            js["x"].asDouble,
            js["y"].asDouble,
            js["z"].asDouble,
            js["yaw"].asFloat,
            js["pitch"].asFloat
            )
    }
}