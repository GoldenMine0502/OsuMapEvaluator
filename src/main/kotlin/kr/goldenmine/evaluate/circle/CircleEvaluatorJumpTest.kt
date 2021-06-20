package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorJumpTest: CircleSeparatedEvaluator {
    override val type: String
        get() = "jumpTest"

    private val evaluatorJump2 = CircleEvaluatorJump2()

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        return evaluatorJump2.evaluate(beatmap, lastHitObject, currentHitObject, mods) * currentHitObject.getAttribute("density multiplier") as Double * currentHitObject.getAttribute("jumpVariance") as Double

    }
}