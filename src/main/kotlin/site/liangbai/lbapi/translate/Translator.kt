package site.liangbai.lbapi.translate

import org.bukkit.entity.Player

object Translator {
    fun String.translate(type: TransType, obj: Any?, player: Player? = null): String {
        return type.func(obj, this, player)
    }

    fun getDefaultTransTypes() = TransType.defaultValues()

    fun List<TransType>.applyTranslate(original: String, obj: Any?, player: Player? = null): String {
        var str = original
        this.forEach {
            str = it.func(obj, original, player)
        }
        return str
    }
}