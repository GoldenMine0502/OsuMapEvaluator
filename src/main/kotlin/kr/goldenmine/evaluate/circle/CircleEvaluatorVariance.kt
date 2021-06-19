package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorVariance: CircleSeparatedEvaluator {
    override val type: String
        get() = "variance"

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        return currentHitObject.getAttributes()["jumpVariance"] as Double
    }
}