package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorTerm: CircleSeparatedEvaluator {
    override val type: String
        get() = "term"

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        return (currentHitObject.startOffset - lastHitObject.startOffset).toDouble()
    }
}