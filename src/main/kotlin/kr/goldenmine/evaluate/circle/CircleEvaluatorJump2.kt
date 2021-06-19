package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorJump2: CircleSeparatedEvaluator {
    override val type: String
        get() = "jump2"

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        return (currentHitObject.getAttributes()["jump2"] as Double) / (currentHitObject.startOffset - lastHitObject.startOffset)
    }
}