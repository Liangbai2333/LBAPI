package site.liangbai.lbapi

import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin

@PlatformSide(Platform.BUKKIT)
object LBAPI : Plugin() {
    val plugin by lazy { BukkitPlugin.getInstance() }

    override fun onEnable() {
        infoColored("&e--------&f[&6LBAPI&f]&e--------")
        infoColored("&a成功载入LBAPI, 欢迎使用靓白系列插件")
        infoColored("&e-------------------------------")
    }

    fun infoColored(text: String) { info(text.colored()) }
}