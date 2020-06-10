package kr.goldenmine.evaluator

import kr.goldenmine.files.Beatmap
import org.nield.kotlinstatistics.standardDeviation
import kotlin.math.abs
import kotlin.math.sqrt

class EvaluatorDistanceVarienceMultiply: BeatmapEvaluator {
    override val type: String
        get() = "Distance Varience(Multiply)"

    override fun evaluate(beatmap: Beatmap, mods: Int): Any? {
        val list = ArrayList<Double>()

        for(i in 1 until beatmap.hitObjects.size - 1) {
            val previousHitObject = beatmap.hitObjects[i - 1]
            val currentHitObject = beatmap.hitObjects[i]
            val nextHitObject = beatmap.hitObjects[i + 1]

            val distancePastToCurrent = (currentHitObject.startPosition - previousHitObject.endPosition).length / (currentHitObject.startOffset - previousHitObject.finishOffset)
            val distanceCurrentToNext = (nextHitObject.startPosition - currentHitObject.endPosition).length / (nextHitObject.startOffset - currentHitObject.finishOffset)
            val gap = if(distanceCurrentToNext > 0 && distancePastToCurrent > 0) abs(distanceCurrentToNext / distancePastToCurrent) else abs(distanceCurrentToNext - distancePastToCurrent)

            val adaptedGap = if(gap < 1) if(gap < 0.00001) 0.0 else 1.0/gap else gap

            //println("offset: ${currentHitObject.startOffset} gap: $adaptedGap")
            if(adaptedGap > 0)
                list.add(adaptedGap)
        }

        val average = list.average()
        val deviation = list.standardDeviation()

        return "average($average), deviation($deviation), multiply(${sqrt(deviation * average)}), subtract(${abs(deviation - average)})"
    }

}