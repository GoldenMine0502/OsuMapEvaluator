package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

interface CircleSeparatedEvaluator : CircleEvaluator {
    fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double

    override fun evaluate(beatmap: Beatmap, index: Int, mods: Int): CircleEvaluatorResult {
        val lastResult = if(index > 0) evaluate(beatmap, beatmap.hitObjects[index - 1], beatmap.hitObjects[index], mods) else 0.0
        val nextResult = if(index < beatmap.hitObjects.size - 1) evaluate(beatmap, beatmap.hitObjects[index], beatmap.hitObjects[index + 1], mods) else 0.0

        return CircleEvaluatorResult(lastResult, nextResult)
    }
}