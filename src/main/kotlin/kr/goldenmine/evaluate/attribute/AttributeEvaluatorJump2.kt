package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.Slider
import kr.goldenmine.files.convertCStoRadius
import kr.goldenmine.util.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class AttributeEvaluatorJump2 : IAttributeEvaluator {
    override fun calculateAttribute(beatmap: Beatmap, mods: Int) {
        val cs = beatmap.convertCStoRadius()
        if (beatmap.hitObjects.size >= 1) {
            beatmap.hitObjects[0].addAttribute("jump2", 0.0)
        }
        if (beatmap.hitObjects.size >= 2) {
            val distance = ((beatmap.hitObjects[1].startPosition - beatmap.hitObjects[0].startPosition).length - 2 * cs).coerceAtLeast(0.0)


            beatmap.hitObjects[1].addAttribute("jump2", distance)
        }

        val positions = ArrayList<Pair<Int, Point>>()

        for (index in beatmap.hitObjects.indices) {
            if (index > 0 && index < beatmap.hitObjects.size - 1) {
                // 슬라이더 디스턴스 계산시 첫 서클부터 다음 서클까지는 distance - r1 - r2
                // else -> like below
                val previous = beatmap.hitObjects[index - 1]
                val current = beatmap.hitObjects[index]
                val next = beatmap.hitObjects[index + 1]

                val currentCalculatedPos = calculateCirclePosition((previous.getAttribute("endPosition") as List<Point>).last(), current.startPosition, cs)
                val nextCalculatedPos = calculateCirclePosition((current.getAttribute("endPosition") as List<Point>).last(), next.startPosition, cs)

                positions.add(Pair(index, currentCalculatedPos))

                val travelDistance = if (current is Slider) { // calculateTravelDistance
                    val dots = current.getAttribute("endPosition") as List<Point>
                    val endOffset = current.getAttribute("endOffset") as Int

//                    if(dots.size > 0) {
                    var distance = ((dots[0] - current.startPosition).length - 2 * cs).coerceAtLeast(0.0)
                    for (dotIndex in dots.indices) {
//                        if (dotIndex < dots.size - 1) {
                            val previousPos = if(dotIndex > 0) dots[dotIndex - 1] else current.startPosition
                            val currentPos = dots[dotIndex]
                            val nextPos = if(dotIndex < dots.size - 1) dots[dotIndex + 1] else next.startPosition

                            val currentCalculatedPosIn = calculateCirclePosition(previousPos, currentPos, cs)
                            val nextCalculatedPosIn = calculateCirclePosition(currentPos, nextPos, cs)

                            positions.add(Pair(index, currentCalculatedPosIn))
//                            println(currentCalculatedPos)

                            distance += (nextCalculatedPosIn - currentCalculatedPosIn).length
//                        }
//                        }
                    }
                    distance
                } else { 0.0 }




                next.addAttribute("jump2", (nextCalculatedPos - currentCalculatedPos).length + travelDistance / 2)
                next.addAttribute("travelDistance", travelDistance)
//                next.addAttribute("jump2pos", currentCalculatedPos)
//                next.addAttribute("jump2pos2", nextCalculatedPos)
            }
        }
        beatmap.addAttribute("positions", positions)
    }

    fun calculateCirclePosition(pos: Point, pos2: Point, cs: Double): Point {
        val distance = (pos2 - pos)

        val current = distance.run { atan2(y, x) }
        val calculatedPos = pos2 + Point(
            -cs * cos(current),
            -cs * sin(current)
        ) * (((cs / 2) * distance.length).coerceAtMost(1.0))

        return calculatedPos
    }
}