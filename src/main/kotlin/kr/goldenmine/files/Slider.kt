package kr.goldenmine.files

import kr.goldenmine.util.Point
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

val circularArcTolerance = 0.1f
val bezierTolerance = 0.25f

data class Slider(
    val points: List<SliderDot>,
    override val startOffset: Int,
    val timingPoint: TimingPoint,
    val lengthPixel: Double,
    val type: Type
) : HitObject {

    enum class Type(val chracter: String) {
        STRAIGHT("L"), CURVE("P"), BEZIER("B")
    }

    override val startPosition: Point
        get() = path.first()
    override val endPosition: Point
        get() = path.last()

    override val finishOffset: Int
        get() = startOffset + (timingPoint.bpm * sliderLength()).toInt()

    val attributesPrivate = HashMap<String, Any>()

    override fun getAttributes(): HashMap<String, Any> = attributesPrivate
    override fun addAttribute(key: String, value: Any) {
        attributesPrivate[key] = value
    }

    val path: List<Point>

    init {
        fun adaptResult(result: List<Point>): List<Point> {
            var partLength = 0.0
            var minIndex = result.size
            val lengthSlider = lengthPixel

            for (i in 0 until result.size - 1) {
                partLength += (result[i + 1] - result[i]).length
                if (partLength >= lengthSlider) {
                    minIndex = i + 1
                    break
                }
            }

            return result.subList(0, minIndex)
        }

        when (type) {
            Type.STRAIGHT -> {
                val startPos = points[0]
                val finishPos = points[1]

                val direction = (finishPos.point - startPos.point)
                val angle = atan2(direction.y, direction.x)
                val actualFinishPos =
                    Point(startPos.point.x + lengthPixel * cos(angle), startPos.point.y + lengthPixel * sin(angle))

                path = listOf(startPos.point, actualFinishPos)
            }

            Type.CURVE -> {
                val result = curveSlider()

                path = result
            }

            Type.BEZIER -> {
                //val bezier = BezierLine()

                val buffer = ArrayList<Point>()
                val result = ArrayList<Point>()

                for (i in points.indices) {
                    val point = points[i]
                    buffer.add(point.point)
                    if (i == points.size - 1 || point.dotType == DotType.STRAIGHT) {
                        result.addAll(bezierSlider(ArrayList(buffer)))
                        result.removeAt(result.size - 1)

                        buffer.clear()
                        buffer.add(point.point)
                    }
                }

                result.add(points.last().point)

                path = result
            }
            //else -> throw RuntimeException("slider type is strange: $type")
        }
    }

    fun sliderLength(): Double {
        val adaptedSliderVelocity = timingPoint.bpm * timingPoint.sliderVelocity

        return lengthPixel / adaptedSliderVelocity
    }

    fun bezierSlider(controlPoints: List<Point>): List<Point> {
        val output = ArrayList<Point?>()
        val count = controlPoints.size

        if (count > 0) {
            val subdivisionBuffer1 = arrayOfNulls<Point>(count)
            val subdivisionBuffer2 = arrayOfNulls<Point>(count * 2 + 1)

            val toFlatten = Stack<Array<Point?>>()
            val freeBuffers = Stack<Array<Point?>>()

            toFlatten.push(controlPoints.toTypedArray())

            val leftChild = subdivisionBuffer2

            while (toFlatten.size > 0) {
                val parent = toFlatten.pop()

                if (isBezierFlatEnough(parent)) {
                    bezierApproximate(parent, output, subdivisionBuffer1, subdivisionBuffer2, count)

                    freeBuffers.push(parent)
                    continue
                }

                val rightChild = if (freeBuffers.size > 0) freeBuffers.pop() else arrayOfNulls(count)
                bezierSubdivide(parent, leftChild, rightChild, subdivisionBuffer1, count)

                for (i in 0 until count)
                    parent[i] = leftChild[i]

                toFlatten.push(rightChild)
                toFlatten.push(parent)
            }
        }

        output.add(controlPoints.last())

        return output.map { it!! }.toList()
    }

    /*

    private static void bezierApproximate(Vector2[] controlPoints, List<Vector2> output, Vector2[] subdivisionBuffer1, Vector2[] subdivisionBuffer2, int count)
        {
            Vector2[] l = subdivisionBuffer2;
            Vector2[] r = subdivisionBuffer1;

            bezierSubdivide(controlPoints, l, r, subdivisionBuffer1, count);

            for (int i = 0; i < count - 1; ++i)
                l[count + i] = r[i + 1];

            output.Add(controlPoints[0]);

            for (int i = 1; i < count - 1; ++i)
            {
                int index = 2 * i;
                Vector2 p = 0.25f * (l[index - 1] + 2 * l[index] + l[index + 1]);
                output.Add(p);
            }
        }
     */

    fun bezierApproximate(
        parent: Array<Point?>,
        output: MutableList<Point?>,
        subdivisionBuffer: Array<Point?>,
        subdivisionBuffer2: Array<Point?>,
        count: Int
    ) {
        val l = subdivisionBuffer2
        val r = subdivisionBuffer

        bezierSubdivide(parent, l, r, subdivisionBuffer, count)

        for (i in 0 until count - 1)
            l[count + i] = r[i + 1]

        output.add(parent[0])

        for (i in 1 until count - 1) {
            val index = 2 * i
            val p = (l[index - 1]!! + l[index]!! * 2 + l[index + 1]!!) * 0.25

            output.add(p)
        }
    }

    fun bezierSubdivide(
        parent: Array<Point?>,
        l: Array<Point?>,
        r: Array<Point?>,
        subdivisionBuffer: Array<Point?>,
        count: Int
    ) {
        val midpoints = subdivisionBuffer

        for (i in 0 until count)
            midpoints[i] = parent[i]

        for (i in 0 until count) {
            l[i] = midpoints[0]
            r[count - i - 1] = midpoints[count - i - 1]

            for (j in 0 until count - i - 1)
                midpoints[j] = (midpoints[j]!! + midpoints[j + 1]!!) / 2
        }
    }

    fun isBezierFlatEnough(points: Array<Point?>): Boolean {
        for (i in 1 until points.size - 1) {
            if ((points[i - 1]!! - points[i]!! * 2 + points[i + 1]!!).lengthSquared > bezierTolerance * bezierTolerance * 4)
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

        var orthoAtoC = c - a
        orthoAtoC = Point(orthoAtoC.y, -orthoAtoC.x)

        if (orthoAtoC.dot(b - a) < 0) {
            dir = -dir
            thetaRange = 2 * Math.PI - thetaRange
        }

        val amountPoints = if (2 * r <= circularArcTolerance)
            2
        else
            ceil(thetaRange / (2 * acos(1 - circularArcTolerance / r))).coerceAtLeast(2.0).toInt()

        val output = ArrayList<Point>(amountPoints)
        output.add(a)

        for (i in 0 until amountPoints) {
            val fract: Double = i.toDouble() / (amountPoints - 1)
            val theta = thetaStart + dir * fract * thetaRange
            val o = Point(cos(theta), sin(theta)) * r
            output.add(circumcentre + o)
        }

        output.add(c)

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