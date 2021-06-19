package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorDistance: CircleSeparatedEvaluator {
    override val type: String
        get() = "distance"

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        return (currentHitObject.startPosition - lastHitObject.startPosition).length
    }
}