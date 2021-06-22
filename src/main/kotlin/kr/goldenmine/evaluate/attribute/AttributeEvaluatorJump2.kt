package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.convertCStoRadius
import kr.goldenmine.util.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class AttributeEvaluatorJump2 : IAttributeEvaluator {
    override fun calculateAttribute(beatmap: Beatmap, mods: Int) {
        val cs = beatmap.convertCStoRadius()
        if (beatmap.hitObjects.size >= 1) {
            beatmap.hitObjects[0].addAttribute("jump2", 0.0)
        }
        if (beatmap.hitObjects.size >= 2) {
            beatmap.hitObjects[1].addAttribute("jump2", 0.0)
        }

        for (index in beatmap.hitObjects.indices) {
            if (index > 0 && index < beatmap.hitObjects.size - 1) {
                // 슬라이더 디스턴스 계산시 첫 서클부터 다음 서클까지는 distance - r1 - r2
                // else -> like below
                val previous = beatmap.hitObjects[index - 1]
                val current = beatmap.hitObjects[index]
                val next = beatmap.hitObjects[index + 1]

                val distanceCurrent = current.startPosition - previous.startPosition
                val distanceNext = next.startPosition - current.startPosition
                val angleCurrent = distanceCurrent.run { atan2(y, x) }
                val angleNext = distanceNext.run { atan2(y, x) }

                val currentCalculatedPos = current.startPosition + Point(
                    -cs * cos(angleCurrent),
                    -cs * sin(angleCurrent)
                ) * (((cs / 2) * distanceCurrent.length).coerceAtMost(1.0))
                val nextCalculatedPos = next.startPosition + Point(
                    -cs * cos(angleNext),
                    -cs * sin(angleNext)
                ) * (((cs / 2) * distanceNext.length).coerceAtMost(1.0))

                next.addAttribute("jump2", (nextCalculatedPos - currentCalculatedPos).length)
                next.addAttribute("jump2pos", currentCalculatedPos)
                next.addAttribute("jump2pos2", nextCalculatedPos)
            }
        }
    }
}