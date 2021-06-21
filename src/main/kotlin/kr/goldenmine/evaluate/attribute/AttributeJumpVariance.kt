package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.convertCStoRadius
import kr.goldenmine.util.Point
import kotlin.math.*

class AttributeJumpVariance: IAttribute {
    override fun calculateAttribute(beatmap: Beatmap, mods: Int) {
        if(beatmap.hitObjects.size >= 1){
            beatmap.hitObjects[0].addAttribute("jumpVariance", 1.0)
        }
        if(beatmap.hitObjects.size >= 2){
            beatmap.hitObjects[1].addAttribute("jumpVariance", 1.0)
        }
        if(beatmap.hitObjects.size >= 1){
            beatmap.hitObjects.last().addAttribute("jumpVariance", 1.0)
        }
        for(index in beatmap.hitObjects.indices) {
            if(index > 0 && index < beatmap.hitObjects.size - 1) {
                val previous = beatmap.hitObjects[index - 1]
                val current = beatmap.hitObjects[index]
                val next = beatmap.hitObjects[index + 1]

                val distanceNext = (next.startPosition - current.startPosition).length
                val distancePrevious = (current.startPosition - previous.startPosition).length

                val variance = log2(1 + (distanceNext / distancePrevious).run { if(this < 1) 1 / this else this }.coerceAtMost(2.0).coerceAtLeast(1.0))

                next.addAttribute("jumpVariance", variance)
            }
        }
    }
}