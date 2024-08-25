package site.liangbai.lbapi.util

import taboolib.common.platform.function.submit

// unused
class CooldownTask {
    private val cache = mutableListOf<String>()

    fun push(name: String, cooldown: Int) {
        cache.add(name)

        submit(delay = ((cooldown * 20).toLong())) {
            pop(name)
        }
    }

    fun pop(name: String) {
        cache.remove(name)
    }

    operator fun contains(name: String): Boolean {
        return name in cache
    }

    companion object {
        fun newTask(): CooldownTask {
            return CooldownTask()
        }
    }
}