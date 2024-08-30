package site.liangbai.lbapi.util

fun findClassOrNull(clazz: String): Class<*>? {
    return try {
        Class.forName(clazz)
    } catch (e: ClassNotFoundException) {
        null
    }
}