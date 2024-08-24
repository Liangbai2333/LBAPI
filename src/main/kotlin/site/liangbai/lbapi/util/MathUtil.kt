package site.liangbai.lbapi.util

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

val engine: ScriptEngine by lazy {
    ScriptEngineManager().getEngineByName("JavaScript")
}

fun String.calculate(): Double {
    return engine.eval(this) as Double
}