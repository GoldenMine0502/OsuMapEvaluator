package kr.goldenmine.evaluate.evaluator

import kr.goldenmine.files.Beatmap
import kotlin.math.abs

class EvaluatorVelocity : BeatmapEvaluator {
    override val type: String
        get() = "Velocity Score"

    override fun evaluate(beatmap: Beatmap, mods: Int): Any? {
        var differenceSum = 0.0
        var differenceCount = 0
        //println("size: ${beatmap.timingPoints.size}, ${beatmap.timingPoints}")
        for(i in 1 until beatmap.timingPoints.size) {
            val pastTimingPoint = beatmap.timingPoints[i - 1]
            val currentTimingPoint = beatmap.timingPoints[i]

            if (currentTimingPoint.offset - pastTimingPoint.offset >= 1) {
                val difference = abs(currentTimingPoint.bpm * currentTimingPoint.sliderVelocity - pastTimingPoint.bpm * pastTimingPoint.sliderVelocity) / (currentTimingPoint.offset - pastTimingPoint.offset) * 100

                differenceSum += difference
                differenceCount++
            }
        }

        return "sum(${String.format("%.2f", differenceSum)}) average(${String.format("%.2f", differenceSum / differenceCount)})"
    }
}