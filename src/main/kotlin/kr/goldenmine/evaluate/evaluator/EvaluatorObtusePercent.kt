package kr.goldenmine.evaluate.evaluator

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.Circle
import kr.goldenmine.util.angle
import kotlin.math.abs

class EvaluatorObtusePercent : BeatmapEvaluator {
    override val type: String
        get() = "Obtuse percent"

    val hardestAngle = 150.0

    override fun evaluate(beatmap: Beatmap, mods: Int): Any? {
        val hitObjects = beatmap.hitObjects

        var count = 0.0
        var score = 0.0

        for(i in 1 until hitObjects.size - 1) {
            val pastHitObject = hitObjects[i - 1]
            val currentHitObject = hitObjects[i]
            val nextHitObject = hitObjects[i + 1]

            if((pastHitObject is Circle) && (currentHitObject is Circle) /*&& (nextHitObject is Circle)*/ && (nextHitObject.startPosition - currentHitObject.startPosition).length > 1) {
                val angle = angle(pastHitObject.startPosition, currentHitObject.startPosition, nextHitObject.startPosition)
                val angleScore = if(angle >= 90) {
                    (hardestAngle-abs(hardestAngle - angle) * 2.5) / hardestAngle
                } else 0.0

                score += angleScore
            }

            count++
        }

        return "${String.format("%.2f", score / count * 100)}%"
    }

}