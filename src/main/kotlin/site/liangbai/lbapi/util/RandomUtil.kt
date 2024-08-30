package site.liangbai.lbapi.util

import kotlin.random.Random

fun <T> weightedRandomChoice(weightsMap: List<Pair<Int, T>>): T {
    val weightsList = weightsMap.map { it.first }

    return weightsMap[weightedRandomChoice(weightsList)].second
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
    val chance: Double = if (percent) {
        probability / 100.0
    } else {
        probability
    }
    val randomValue = Random.nextDouble()
    return randomValue < chance
}