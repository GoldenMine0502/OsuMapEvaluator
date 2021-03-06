package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject
import kr.goldenmine.util.Mods

interface CircleEvaluator {
    val type: String

    fun evaluate(beatmap: Beatmap, index: Int, mods: Int): CircleEvaluatorResult

}

fun dtMultiplier(mods: Int): Double {
    return (if(mods and Mods.DT.value > 0) 2.0/3.0 else 1.0)
}