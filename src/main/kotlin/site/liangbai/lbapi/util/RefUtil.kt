package site.liangbai.lbapi.util

import taboolib.library.reflex.ClassField
import taboolib.library.reflex.ClassMethod
import taboolib.library.reflex.ReflexClass

/**
 * 执行方法
 * @param name 方法名称
 * @param parameter 方法参数
 * @param isStatic 是否为静态方法
 * @param findToParent 是否查找父类方法
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.invokeMethodSilently(name: String, vararg parameter: Any?, isStatic: Boolean = false, findToParent: Boolean = true, remap: Boolean = true): T? {
    return if (isStatic && this is Class<*>) {
        ReflexClass.of(this).getMethodSilently(name, findToParent, remap, *parameter)?.invokeStatic(*parameter) as T?
    } else {
        ReflexClass.of(javaClass).getMethodSilently(name, findToParent, remap, *parameter)?.invoke(this, *parameter) as T?
    }
}

fun ReflexClass.getMethodSilently(name: String, vararg parameter: Any?): ClassMethod? {
    return this.structure.getMethodSilently(name, *parameter)
}

fun ReflexClass.getFieldSilently(name: String): ClassField? {
    return this.structure.getFieldSilently(name)
}

/**
 * 获取字段
 * @param path 字段名称，使用 "/" 符号进行递归获取
 * @param isStatic 是否为静态字段
 * @param findToParent 是否查找父类字段
 */
fun <T> Any.getPropertySilently(path: String, isStatic: Boolean = false): T? {
    return if (path.contains('/')) {
        val left = path.substringBefore('/')
        val right = path.substringAfter('/')
        getLocalPropertySilently<Any>(left, isStatic)?.getPropertySilently(right, isStatic)
    } else {
        getLocalPropertySilently(path, isStatic)
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T> Any.getLocalPropertySilently(name: String, isStatic: Boolean = false): T? {
    return if (isStatic && this is Class<*>) {
        ReflexClass.of(this).getFieldSilently(name)?.get() as T?
    } else {
        ReflexClass.of(javaClass).getFieldSilently(name)?.get(this) as T?
    }
}