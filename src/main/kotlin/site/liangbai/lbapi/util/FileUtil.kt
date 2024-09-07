package site.liangbai.lbapi.util

import site.liangbai.lbapi.LBAPI
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin
import java.io.File
import java.io.InputStream

fun releaseResourceFileIfFolderEmpty (source: String, target: String = source, replace: Boolean = false) {
    val file = File(getDataFolder(), target)
    if (file.exists() && !replace) {
        return
    }
    val parent = file.parentFile
    if (!parent.exists()) {
        parent.mkdirs()
    }
    if (file.parentFile.listFiles()!!.isEmpty()) {
        newFile(file).writeBytes(BukkitPlugin.getInstance().getResource(source)?.readBytes() ?: error("resource not found: $source"))
    }
}

// error
fun releaseFolderResourceFiles(source: String, target: String = source, replace: Boolean = false) {
    val parent = File(getDataFolder(), target)
    if (parent.exists() && !replace) {
        return
    }
    if (!parent.exists()) {
        parent.mkdirs()
    }

    if (parent.listFiles()!!.isEmpty()) {
        getResources(source)?.forEach { (fileName, input) ->
            input.use {
                newFile(File(parent, fileName)).writeBytes(input.readBytes())
            }
        } ?: error("resource not found: $source")
    }
}

fun withFolderConfigFiles(path: String): List<ConfigFile> {
    return File(getDataFolder(), path).listFiles()!!.filter { it.extension == "yml" }.map { Configuration.loadFromFile(it) as ConfigFile }
}

fun withFolderFiles(path: String): List<File> {
    return File(getDataFolder(), path).listFiles()!!.filter { it.extension == "yml" }
}

fun getResources(path: String): Map<String, InputStream>? {
    val out = HashMap<String, InputStream>()
    try {
        val classLoader = LBAPI.javaClass.classLoader
        val urls = classLoader.getResources("$path/")
        while (urls.hasMoreElements()) {
            val url = urls.nextElement()
            out[getFileNameFromAbsolutePath(url.path)] = url.openStream()
        }
    } catch (e: Exception) {
        return null
    }

    return out
}

fun getFileNameFromAbsolutePath(path: String): String {
    return path.substringAfterLast("/")
}