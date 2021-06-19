package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.convertCStoRadius
import kr.goldenmine.util.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class AttributeJump2: IAttribute {
    override fun calculateAttribute(beatmap: Beatmap) {
        val cs = beatmap.convertCStoRadius()
        for(index in beatmap.hitObjects.indices) {
            if(index > 0 && index < beatmap.hitObjects.size - 1) {
                val previous = beatmap.hitObjects[index - 1]
                val current = beatmap.hitObjects[index]
                val next = beatmap.hitObjects[index + 1]

                val angleCurrent = (current.startPosition - previous.startPosition).run { atan2(y, x) }
                val angleNext = (next.startPosition - current.startPosition).run { atan2(y, x) }

                val currentCalculatedPos = current.startPosition + Point(-cs * cos(angleCurrent), -cs * sin(angleCurrent))
                val nextCalculatedPos = next.startPosition + Point(-cs * cos(angleNext), -cs * sin(angleNext))

                next.addAttribute("jump2", (nextCalculatedPos - currentCalculatedPos).length)
                next.addAttribute("jump2pos", currentCalculatedPos)
                next.addAttribute("jump2pos2", nextCalculatedPos)
            }
        }
    }
}