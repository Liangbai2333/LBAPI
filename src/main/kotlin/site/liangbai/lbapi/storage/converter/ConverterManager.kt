package site.liangbai.lbapi.storage.converter

import com.google.gson.*
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import site.liangbai.lbapi.storage.converter.impl.ItemStackConverter
import site.liangbai.lbapi.storage.converter.impl.ListConverter
import site.liangbai.lbapi.storage.converter.impl.StorableInventory
import site.liangbai.lbapi.storage.converter.impl.ValueMapConverter
import site.liangbai.lbapi.storage.converter.impl.bean.Bean
import site.liangbai.lbapi.storage.converter.impl.bean.BeanConverter
import site.liangbai.lbapi.storage.converter.impl.forge.PokemonConverter
import taboolib.common.platform.Platform
import taboolib.common.platform.function.runningPlatform
import taboolib.library.reflex.Reflex.Companion.invokeConstructor

object ConverterManager {
    private val converters = mutableMapOf<Class<*>, Class<out IConverter<out Any>>>()
    private val nameConverters = mutableMapOf<String, Class<out IConverter<out Any>>>()

    private val converterInstances = mutableMapOf<Class<*>, IConverter<out Any>>()
    val parser = JsonParser()
    val gson = Gson()

    init {
        if (runningPlatform == Platform.BUKKIT) {
            registerConverter(ItemStack::class.java, ItemStackConverter::class.java)
            registerConverter(Inventory::class.java, StorableInventory::class.java)
        }
        registerConverter(Map::class.java, ValueMapConverter::class.java)
        registerConverter(MutableList::class.java, ListConverter::class.java)
        registerConverter(Bean::class.java, BeanConverter::class.java)
        if (Class.forName("net.minecraftforge.common.MinecraftForge") != null) {
            if (Class.forName("com.pixelmonmod.pixelmon.Pixelmon") != null) {
                registerConverter(Pokemon::class.java, PokemonConverter::class.java)
            }
        }
    }

    private fun registerConverter(clazz: Class<*>, converter: Class<out IConverter<out Any>>) {
        converters[clazz] = converter
        nameConverters[converter.simpleName] = converter
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> Class<out IConverter<out T>>.findInstance(): IConverter<out T> {
        return converterInstances.computeIfAbsent(this) {
            it.invokeConstructor() as IConverter<T>
        } as IConverter<T>
    }

    @Suppress("UNCHECKED_CAST")
    private fun matchConverter(clazz: Class<*>): IConverter<Any> {
        for (converter in converters) {
            if (converter.key.isAssignableFrom(clazz)) {
                return converter.value.findInstance() as IConverter<Any>
            }
        }

        return EmptyConverter
    }

    internal object EmptyConverter : IConverter<Any> {
        override fun convertToElement(value: Any): JsonElement {
            return JsonPrimitive(value.toString())
        }

        override fun convertFromString(data: JsonElement): Any {
            return data.asString
        }
    }

    private fun Any.convertToJsonObject(): JsonObject {
        val jsonObject = JsonObject()
        val converter = matchConverter(this::class.java)
        jsonObject.addProperty("converter", converter::class.java.simpleName)
        jsonObject.add("data", converter.convertToElement(this))
        return jsonObject
    }

    fun Any.convertToElement(): JsonElement {
        if (this is String || this::class.javaPrimitiveType != null) {
            return if (this is Number) {
                JsonPrimitive(this)
            } else if (this is Boolean) {
                JsonPrimitive(this)
            } else if (this is String) {
                JsonPrimitive(this)
            } else JsonPrimitive(this.toString())

        }

        return convertToJsonObject()
    }

    fun Any.convertToString(): String {
        return convertToElement().toString()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> JsonElement.convertToEntity(): T {
        if (!isJsonObject) {
            if (isJsonPrimitive) {
                val j = asJsonPrimitive
                if (j.isNumber) {
                    j.asNumber as T
                } else if (j.isBoolean) {
                    j.asBoolean as T
                } else if (j.isString) {
                    j.asString as T
                }
            }
            throw IllegalStateException("Invalid type $this")
        }
        val jsonObject: JsonObject = this as JsonObject
        val converter = nameConverters[jsonObject.get("converter").asString]!!.findInstance()
        val data = jsonObject.get("data")
        return converter.convertFromString(data) as T
    }

    fun <T> String.convertToEntity(): T {
        return parser.parse(this).convertToEntity()
    }
}