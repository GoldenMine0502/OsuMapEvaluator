package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorAdaptedTerm: CircleSeparatedEvaluator {
    override val type: String
        get() = "term_adapted"

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        return currentHitObject.getAttributes()["adaptedTerm"] as Double
    }
}