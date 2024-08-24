package site.liangbai.lbapi.util

import kotlin.random.Random

fun <T> weightedRandomChoice(weightsMap: Map<Int, T>): T {
    val weightsList = weightsMap.keys.toList()
    return weightsMap[weightsList[weightedRandomChoice(weightsList)]]!!
}

fun weightedRandomChoice(weights: List<Int>): Int {
    val totalWeight = weights.sum()
    var randomValue = Random.nextInt(totalWeight)
    for ((index, weight) in weights.withIndex()) {
        randomValue -= weight
        if (randomValue < 0) return index
    }
    throw IllegalArgumentException("Weights must be non-empty and non-negative.")
}

fun checkProbability(probability: Double, percent: Boolean = false): Boolean {
    require(probability in 0.0..100.0) { "Probability must be between 0 and 100" }
    var chance: Double = if (percent) {
        probability / 100.0
    } else {
        probability
    }
    val randomValue = Random.nextDouble()
    return randomValue < chance
}