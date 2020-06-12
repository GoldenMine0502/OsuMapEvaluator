package kr.goldenmine.evaluate.evaluator

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.Circle
import kr.goldenmine.util.angle
import kotlin.math.abs

class EvaluatorObtusePercentWithDistance : BeatmapEvaluator {
    override val type: String
        get() = "Obtuse percent(distance)"

    val hardestAngle = 150.0

    override fun evaluate(beatmap: Beatmap, mods: Int): Any? {
        val hitObjects = beatmap.hitObjects

        var count = 0.0
        var score = 0.0

        for(i in 1 until hitObjects.size - 1) {
            //println(hitObjects[i].startOffset)
            val pastHitObject = hitObjects[i - 1]
            val currentHitObject = hitObjects[i]
            val nextHitObject = hitObjects[i+1]

            val distance = (nextHitObject.startPosition - currentHitObject.startPosition).length / (nextHitObject.startOffset - currentHitObject.finishOffset)

            if(pastHitObject is Circle && currentHitObject is Circle && nextHitObject is Circle) {
                val angle = angle(pastHitObject.startPosition, currentHitObject.startPosition, nextHitObject.startPosition)
                val angleScore = if(angle >= 90) {
                    (hardestAngle-abs(hardestAngle - angle)) / hardestAngle
                } else 0.0

                score += angleScore * distance
            }
            count += distance

            // 150 = 1,
        }

        return "${String.format("%.2f", score / count * 100)}%"
    }

}