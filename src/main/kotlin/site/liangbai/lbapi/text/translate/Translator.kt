package site.liangbai.lbapi.text.translate

import org.bukkit.entity.Player

object Translator {
    fun String.translate(type: TransType, obj: Any?, player: Player? = null): String {
        return type.translator(obj, this, player)
    }

    fun getDefaultTransTypes() = TransType.defaultValues()

    fun Collection<TransType>.applyTranslate(original: String, obj: Any?, player: Player? = null): String {
        var str = original
        this.forEach {
            str = it.translator(obj, original, player)
        }
        return str
    }

    fun registerCustomTranslator(identity: String, default: Boolean, translator: (Any?, String, Player?) -> String) {
        TransType.registerCustomTranslator(identity, default, translator)
    }

    fun getCustomTranslator(identity: String): TransType {
        return TransType.getCustomTranslator(identity)!!
    }
}