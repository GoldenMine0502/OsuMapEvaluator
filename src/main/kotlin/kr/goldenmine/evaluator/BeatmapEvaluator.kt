package kr.goldenmine.evaluator

import kr.goldenmine.files.Beatmap
import kr.goldenmine.util.Mods

interface BeatmapEvaluator {
    val type: String

    fun evaluate(beatmap: Beatmap, mods: Int): Any?

}

fun dtMultiplier(mods: Int): Double {
    return (if(mods and Mods.DT.value > 0) 2.0/3.0 else 1.0)
}