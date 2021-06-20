package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorDensity: CircleSeparatedEvaluator {
    override val type: String
        get() = "density"

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        return (currentHitObject.getAttributes()["density"] as Int).toDouble()
    }
}