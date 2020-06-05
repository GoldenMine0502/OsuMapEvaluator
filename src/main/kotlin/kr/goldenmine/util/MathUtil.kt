package kr.goldenmine.util

import kotlin.math.atan2

fun angle(p1: Point, p2: Point, p3: Point): Double =
    angle(p1.xDouble, p1.yDouble, p3.xDouble, p3.yDouble, p2.xDouble, p2.yDouble)

fun angle(point1X: Double, point1Y: Double,
          point2X: Double, point2Y: Double,
          fixedX: Double, fixedY: Double): Double {
    val angle1 = atan2(point1Y - fixedY, point1X - fixedX)
    val angle2 = atan2(point2Y - fixedY, point2X - fixedX)

    val result = (angle1 - angle2).toDegrees()

    // println(360 - result)

    return if(result > 180) 360 - result else if(result < -180) 360 + result else if(result < 0) -result else result
}

fun Double.toDegrees(): Double = Math.toDegrees(this)
fun Double.toRadians(): Double = Math.toRadians(this)