package kr.goldenmine.aimvalue

import kr.goldenmine.files.Circle
import kr.goldenmine.util.angle
import kotlin.math.max

class AdvancedCircle(
    val previousPoint: Circle?,
    val currentPoint: Circle,
    val nextPoint: Circle?
) {
    val deltaTime
        get() = if(nextPoint != null) nextPoint.offset - currentPoint.offset else 0
    val strainTime
        get() = max(50, deltaTime)
    val angle
        get() = if(previousPoint != null && nextPoint != null)
            angle(previousPoint.point, currentPoint.point, nextPoint.point)
        else 0.0
    val jumpDistance
        get() = if(nextPoint != null)
            (nextPoint.point - currentPoint.point).length else 0.0

    override fun toString(): String {
        return """
            ** $currentPoint **
              deltaTime: $deltaTime
              angle: $angle
              jumpDistance: $jumpDistance
        """.trimIndent()
    }
}