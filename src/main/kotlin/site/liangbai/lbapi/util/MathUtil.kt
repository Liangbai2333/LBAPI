package site.liangbai.lbapi.util

import kotlin.math.floor
import kotlin.math.pow

class ExpressionParser(private val input: String) {
    private var pos = 0
    private var currentChar: Char? = input.getOrNull(pos)

    fun parse(): Double {
        return parseExpression()
    }

    private fun advance() {
        pos++
        currentChar = if (pos < input.length) input[pos] else null
        skipWhitespace()
    }

    private fun skipWhitespace() {
        while (currentChar == ' ') {
            pos++
            currentChar = if (pos < input.length) input[pos] else null
        }
    }

    private fun parseExpression(): Double {
        var result = parseTerm()

        while (currentChar == '+' || currentChar == '-') {
            val operator = currentChar
            advance()

            val term = parseTerm()
            result = if (operator == '+') result + term else result - term
        }

        return result
    }

    private fun parseTerm(): Double {
        var result = parseFactor()

        while (currentChar == '*' || currentChar == '/' || currentChar == '%') {
            val operator = currentChar
            advance()
            val factor = parseFactor()
            result = when (operator) {
                '*' -> result * factor
                '/' -> result / factor
                '%' -> result % factor
                else -> result
            }
        }

        return result
    }

    private fun parseFactor(): Double {
        // 处理一元运算符的逻辑
        var sign = 1
        while (currentChar == '+' || currentChar == '-') {
            if (currentChar == '-') sign *= -1
            advance()
        }

        var result = parseBase()

        // 处理指数运算
        while (currentChar == '^') {
            advance()
            val exponent = parseFactor()
            result = result.pow(exponent)
        }

        return result * sign
    }

    private fun parseBase(): Double {
        return when (currentChar) {
            '(' -> {
                advance() // consume '('
                val result = parseExpression()
                advance() // consume ')'
                result
            }
            else -> parseNumber()
        }
    }

    private fun parseNumber(): Double {
        val start = pos
        while (currentChar?.isDigit() == true || currentChar == '.') {
            advance()
        }
        return input.substring(start, pos).toDouble()
    }
}

fun String.calculate(): Double {
    return ExpressionParser(this).parse()
}

fun Double.truncateWithDecimalPlaces(decimalPlaces: Int): Double {
    return floor(this * 10.0.pow(decimalPlaces)) / 10.0.pow(decimalPlaces)
}

fun Double.getFormatString(decimalPlaces: Int): String {
    return String.format("%." + decimalPlaces + "f", this)
}