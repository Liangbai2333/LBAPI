package site.liangbai.lbapi.util

import site.liangbai.lbapi.storage.converter.impl.bean.Bean
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

fun withCountdown(duration: Long) = CountdownTimer(System.currentTimeMillis() + duration)

class CountdownTimer(private val endTime: Long, private val lengthDefault: Int = 2, private val padStartDefault: Char = '0') : Bean {
    fun getRemainingTime() = endTime - System.currentTimeMillis()

    fun getRemainingSeconds() = (getRemainingTime() / 1000) % 60
    fun getRemainingMinutes() = (getRemainingTime() / (1000 * 60)) % 60
    fun getRemainingHours() = (getRemainingTime() / (1000 * 60 * 60)) % 24
    fun getRemainingDays() = (getRemainingTime() / (1000 * 60 * 60 * 24))

    fun getFormattedDays(length: Int = lengthDefault, padStart: Char = padStartDefault): String {
        return getRemainingDays().toString().padStart(length, padStart)
    }

    fun getFormattedHours(length: Int = lengthDefault, padStart: Char = padStartDefault): String {
        return getRemainingHours().toString().padStart(length, padStart)
    }

    fun getFormattedMinutes(length: Int = lengthDefault, padStart: Char = padStartDefault): String {
        return getRemainingMinutes().toString().padStart(length, padStart)
    }

    fun getFormattedSeconds(length: Int = lengthDefault, padStart: Char = padStartDefault): String {
        return getRemainingSeconds().toString().padStart(length, padStart)
    }
}