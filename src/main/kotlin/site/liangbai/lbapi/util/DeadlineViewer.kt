package site.liangbai.lbapi.util

import site.liangbai.lbapi.storage.converter.impl.bean.Bean

data class DeadlineViewer(val start: Long = System.currentTimeMillis(), var end: Long): Bean {
    fun checkTerminal() = System.currentTimeMillis() > end

    fun runIfTerminal(func: DeadlineViewer.() -> Unit) {
        if (checkTerminal()) func(this)
    }

    fun runIfNotTerminal(func: DeadlineViewer.() -> Unit) {
        if (!checkTerminal()) func(this)
    }
}

fun withDeadline(duration: Long): DeadlineViewer {
    return DeadlineViewer(end = System.currentTimeMillis() + duration)
}