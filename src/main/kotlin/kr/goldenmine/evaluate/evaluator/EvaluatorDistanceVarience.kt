package kr.goldenmine.evaluate.evaluator

import kr.goldenmine.files.Beatmap
import org.nield.kotlinstatistics.standardDeviation
import kotlin.math.abs
import kotlin.math.sqrt

class EvaluatorDistanceVarience: BeatmapEvaluator {
    override val type: String
        get() = "Distance Varience"

    override fun evaluate(beatmap: Beatmap, mods: Int): Any? {
        val list = ArrayList<Double>()

        for(i in 1 until beatmap.hitObjects.size - 1) {
            val previousHitObject = beatmap.hitObjects[i - 1]
            val currentHitObject = beatmap.hitObjects[i]
            val nextHitObject = beatmap.hitObjects[i + 1]

            val distancePastToCurrent = (currentHitObject.startPosition - previousHitObject.endPosition).length / (currentHitObject.startOffset - previousHitObject.finishOffset)
            val distanceCurrentToNext = (nextHitObject.startPosition - currentHitObject.endPosition).length / (nextHitObject.startOffset - currentHitObject.finishOffset)
            val gap = abs(distanceCurrentToNext - distancePastToCurrent)

            list.add(gap)
        }

        val average = list.average()
        val deviation = list.standardDeviation()

        return "average($average), deviation($deviation), multiply(${sqrt(deviation * average)}), subtract(${abs(deviation - average)})"
    }

}