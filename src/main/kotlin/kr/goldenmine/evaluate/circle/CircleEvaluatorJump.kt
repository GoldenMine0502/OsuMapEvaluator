package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorJump: CircleSeparatedEvaluator {
    override val type: String
        get() = "jump"

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        return (currentHitObject.startPosition - lastHitObject.startPosition).length / (currentHitObject.startOffset - lastHitObject.startOffset)
    }
}