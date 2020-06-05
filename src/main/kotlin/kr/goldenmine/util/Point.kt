package kr.goldenmine.util

import kotlin.math.sqrt

data class Point(val x: Int, val y: Int) {
    val length: Double
        get() = sqrt(x.toDouble() * x.toDouble() + y.toDouble() * y.toDouble())

    val xDouble = x.toDouble()
    val yDouble = y.toDouble()

    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    operator fun minus(other: Point): Point {
        return Point(x - other.x, y - other.y)
    }
}