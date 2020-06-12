package kr.goldenmine.evaluate.evaluator

import kr.goldenmine.util.calculateODtoMillis
import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject
import kr.goldenmine.util.calculateBPM
import kotlin.math.abs

class EvaluatorRealbpm: BeatmapEvaluator {
    override val type: String
        get() = "real BPM"

    override fun evaluate(beatmap: Beatmap, mods: Int): Any? {
        val odMillis = calculateODtoMillis(beatmap.OD, mods)
        val buffer = ArrayList<HitObject>()
        val realbpms = ArrayList<Pair<Double, Int>>()
        var term = -1.0

        //println("ODMillis: $ODMillis")



        var i = 0
        while(i < beatmap.hitObjects.size - 1) {
            val current = beatmap.hitObjects[i]
            val next = beatmap.hitObjects[i+1]
            val currentTerm = (next.startOffset - current.startOffset) * dtMultiplier(mods)

            if(term == -1.0 || buffer.isEmpty()) {
                term = currentTerm
                buffer.add(current)
            } else if(abs(currentTerm - term) <= 2) {
                buffer.add(current)
            } else {
                buffer.add(current)
                if(buffer.size >= 2) {
                    val length = (buffer.last().startOffset - buffer.first().startOffset) * dtMultiplier(mods) + odMillis * 2
                    val perLength = length / (buffer.size - 1)
                    val realbpm = calculateBPM(perLength) / 4

                    //println("lengthPer: $perLength bpm: $realbpm offset: ${buffer.first().startOffset} count: ${buffer.size}")

                    realbpms.add(Pair(realbpm, buffer.size))
                    i--
                }

                buffer.clear()
            }
            i++
        }

        var sum = 0.0
        var count = 0.0
        var steminaMin = 0.0
        var decay = 1.0
        var decayPer = 0.05

        realbpms.sortedByDescending { it.first }.forEach {
            sum += it.first * it.second * decay
            count += it.second * decay
            //decay = (decay - decayPer).coerceAtLeast(steminaMin)
        }

        return "max(${String.format("%.2f", realbpms.maxBy { it.first }?.first)}), average(${String.format("%.1f", sum / count)}) BPM"
    }

}