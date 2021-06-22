package kr.goldenmine.evaluate.circle

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.HitObject

class CircleEvaluatorJumpTest: CircleSeparatedEvaluator {
    override val type: String
        get() = "jumpTest"

    private val evaluatorJump2 = CircleEvaluatorJump2()

    override fun evaluate(beatmap: Beatmap, lastHitObject: HitObject, currentHitObject: HitObject, mods: Int): Double {
        val a = (currentHitObject.getAttribute("jump2") as Double)
        val aa = (currentHitObject.getAttribute("travelDistance") as Double)
        val b = currentHitObject.getAttribute("density multiplier") as Double
//        val c = currentHitObject.getAttribute("jumpVariance") as Double
        val d = currentHitObject.getAttribute("adaptedTerm") as Double
        return a * b / d

    }
}