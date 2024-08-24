package site.liangbai.lbapi.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val timeFormatters = mutableMapOf<String, DateTimeFormatter>()

fun Number.secondsToTicks(): Long {
    return (this.toLong() * 20)
}
fun Number.secondsToMills(): Long {
    return (this.toLong() * 1000)
}

fun Number.ticksToSeconds(): Long {
    return (this.toLong() / 20)
}

fun Number.millsToSeconds(): Long {
    return (this.toLong() / 1000)
}

fun Number.secondsToMinutes(): Int {
    return (this.toInt() / 60)
}

fun Number.minutesToSeconds(): Long {
    return (this.toLong() * 60)
}

fun Number.hoursToMinutes(): Int {
    return (this.toInt() * 60)
}

fun Number.minutesToHours(): Int {
    return (this.toInt() / 60)
}

fun Number.minutesToTicks(): Long {
    return minutesToSeconds().secondsToTicks()
}

fun Number.hoursToTicks(): Long {
    return hoursToMinutes().minutesToTicks()
}

fun Number.minutesToMills(): Long {
    return minutesToSeconds().secondsToMills()
}

fun Number.hoursToMillis(): Long {
    return hoursToMinutes().minutesToMills()
}

fun formatTime(pattern: String): String {
    val formatter: DateTimeFormatter = timeFormatters[pattern] ?: DateTimeFormatter.ofPattern(pattern).also { timeFormatters[pattern] = it }

    return formatter.format(LocalDateTime.now())
}