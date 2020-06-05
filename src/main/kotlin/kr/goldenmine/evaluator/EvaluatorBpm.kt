package kr.goldenmine.evaluator

import kr.goldenmine.files.Beatmap
import kotlin.math.abs

class EvaluatorBpm : BeatmapEvaluator {
    override val type: String
        get() = "BPM"

    override fun evaluate(beatmap: Beatmap, mods: Int): Any? {
        val minBpm = beatmap.timingPoints.minBy { it.bpm }
        val maxBpm = beatmap.timingPoints.maxBy { it.bpm }

        return if(minBpm != null && maxBpm != null) {
            if(abs(minBpm.bpm - maxBpm.bpm) <= 0.1) {
                "${minBpm.bpm} BPM"
            } else {
                "${minBpm.bpm}-${maxBpm.bpm} BPM"
            }
        } else {
            "Error"
        }
    }

}