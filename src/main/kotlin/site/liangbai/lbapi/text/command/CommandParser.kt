package site.liangbai.lbapi.text.command

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import site.liangbai.lbapi.text.translate.TransType
import site.liangbai.lbapi.text.translate.Translator
import site.liangbai.lbapi.text.translate.Translator.applyTranslate
import taboolib.common.platform.function.console
import taboolib.expansion.dispatchCommandAsOp
import taboolib.module.chat.colored

object CommandParser {
    fun execute(text: String, player: Player, obj: Any? = null, defaultTranslators: Boolean = true, vararg translators: TransType): Boolean {
        val type = text.substringBefore(":").trim().lowercase()
        val trans = mutableListOf<TransType>()
        if (defaultTranslators) {
            trans.addAll(Translator.getDefaultTransTypes())
        }
        trans.addAll(translators)
        val command = trans.applyTranslate(text.substringAfter(":").trimStart(), obj, player).colored()
        return when (type) {
            "command" -> {
                player.performCommand(command)
                true
            }
            "op" -> {
                player.dispatchCommandAsOp(command)
                true
            }
            "console" -> {
                console().performCommand(command)
                true
            }
            "tell" -> {
                player.sendMessage(command)
                true
            }
            "chat" -> {
                player.chat(command)
                true
            }
            "broadcast" -> {
                Bukkit.broadcastMessage(command)
                true
            }
            else -> false
        }
    }
}