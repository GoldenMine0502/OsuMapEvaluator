package kr.goldenmine.files

import edu.uiuc.cs.charm.BezierLine
import kr.goldenmine.util.Point
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

val circularArcTolerance = 0.1f
val bezierTolerance = 0.25f

data class Slider(
    val points: List<SliderDot>,
    override val startOffset: Int,
    override val finishOffset: Int,
    val type: Type
): HitObject {

    enum class Type(val chracter: String) {
        STRAIGHT("L"), CURVE("P"), BEZIER("B")
    }
    override val startPosition: Point
        get() = points.first().point
    override val endPosition: Point
        get() = points.last().point

    init {

    }

    lateinit var path: List<Point>

    // not osu! lazer, currently osu calculation system
    fun calculatePath() {
        when(type) {
            Type.STRAIGHT -> {
                path = points.map { it.point }.toList()
            }

            Type.CURVE -> {
                path = curveSlider()
            }

            Type.BEZIER -> {
                //val bezier = BezierLine()
                val points = points.map { edu.uiuc.cs.charm.Point(it.point.xInt, it.point.yInt) }.toTypedArray()

                //bezier.try_bezier_generation(points, points.size, )
            }
        }
    }

    fun bezierSlider(subPoints: List<Point>): List<Point> {
        val output = ArrayList<Point>()
        val count = subPoints.size

        if(count > 0) {
            val subdivisionBuffer1 = arrayOfNulls<Point>(count)
            val subdivisionBuffer2 = arrayOfNulls<Point>(count * 2 + 1)

            val toFlatten = Stack<Array<Point>>()
            val freeBuffers = Stack<Array<Point>>()

            toFlatten.push(subPoints.toTypedArray())

            val leftChild = subdivisionBuffer2;

            while(toFlatten.size > 0) {
                val parent = toFlatten.peek()

                if(isBezierFlatEnough(parent)) {

                }
            }
        }

        return output
    }

    fun bezierApproximate(points: Array<Point>): List<Point> {
        TODO("not implemented")
    }

    fun isBezierFlatEnough(points: Array<Point>): Boolean {
        for(i in 1 until points.size - 1) {
            if ((points[i - 1] - points[i] * 2 + points[i + 1]).lengthSquared < bezierTolerance * bezierTolerance * 4)
                return false
        }

        return true
    }

    fun curveSlider(): List<Point> {
        val a = points[0].point
        val b = points[1].point
        val c = points[2].point

        val circumcentre = circumcentre(a, b, c)
        val dA = a - circumcentre
        val dC = c - circumcentre

        val thetaStart: Double = atan2(dA.y, dA.x)
        var thetaEnd: Double = atan2(dC.y, dC.x)

        while (thetaEnd < thetaStart) thetaEnd += 2 * Math.PI

        val r = dA.length

        var dir = 1.0
        var thetaRange = thetaEnd - thetaStart

        val orthoAtoC = Point(a.x - c.x, c.y - a.y)

        if(orthoAtoC.dot(b - a) < 0) {
            dir = -dir
            thetaRange = 2 * Math.PI - thetaRange
        }

        val amountPoints = if (2 * r <= circularArcTolerance)
            2
        else
            ceil(thetaRange / (2 * acos(1 - circularArcTolerance / r))).coerceAtLeast(2.0).toInt()

        val output = ArrayList<Point>(amountPoints)

        for (i in 0 until amountPoints) {
            val fract: Double = i.toDouble() / (amountPoints - 1)
            val theta = thetaStart + dir * fract * thetaRange
            val o = Point(cos(theta), sin(theta)) * r
            output.add(circumcentre + o)
        }

        return output
    }

    fun circumcentre(a: Point, b: Point, c: Point): Point {
        val cx = c.x
        val cy = c.y
        val ax = a.x - cx
        val ay = a.y - cy
        val bx = b.x - cx
        val by = b.y - cy

        val denom: Double = 2 * det(ax, ay, bx, by)
        val numx: Double = det(ay, ax * ax + ay * ay, by, bx * bx + by * by)
        val numy: Double = det(ax, ax * ax + ay * ay, bx, bx * bx + by * by)

        val ccx = cx - numx / denom
        val ccy = cy + numy / denom

        return Point(ccx, ccy)
    }

    private fun det(m00: Double, m01: Double, m10: Double, m11: Double): Double {
        return m00 * m11 - m01 * m10
    }
}