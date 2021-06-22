package kr.goldenmine.util

import kotlin.math.round
import kotlin.math.sqrt

data class Point(val x: Double, val y: Double) {
    val lengthSquared: Double
        get() = x * x + y * y
    val length: Double
        get() = sqrt(lengthSquared)

    val xInt = round(x).toInt()
    val yInt = round(y).toInt()

    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    operator fun minus(other: Point): Point {
        return Point(x - other.x, y - other.y)
    }

    operator fun div(other: Point): Point {
        return Point(x / other.x, y / other.y)
    }

    operator fun div(other: Int): Point {
        return Point(x / other, y / other)
    }

    fun det(other: Point): Double {
        return x * other.y - y * other.x
    }

    fun dot(other: Point): Double {
        return x * other.x + y * other.y
    }

    operator fun times(r: Int): Point {
        return Point(x * r, y * r)
    }

    operator fun times(r: Double): Point {
        return Point(x * r, y * r)
    }

}