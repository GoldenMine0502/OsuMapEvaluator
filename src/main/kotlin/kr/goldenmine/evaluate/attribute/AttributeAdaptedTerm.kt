package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.calculateODPre
import kr.goldenmine.files.convertODto100
import kr.goldenmine.files.convertODto300

class AttributeAdaptedTerm: IAttribute {
    override fun calculateAttribute(beatmap: Beatmap, mods: Int) {
        val odTerm = beatmap.convertODto300(mods)
        var currentTerm = -1
        var lastIndex = 0

        for(index in beatmap.hitObjects.indices) {
            if(index < beatmap.hitObjects.size - 1) {
                val current = beatmap.hitObjects[index]
                val next = beatmap.hitObjects[index + 1]

                val term = next.startOffset - current.startOffset

                if(currentTerm >= 0 && currentTerm + 1 + odTerm < term) {
                    val totalTerm = beatmap.hitObjects[index].startOffset - beatmap.hitObjects[lastIndex].startOffset
                    for(index2 in lastIndex until index) {
                        val lastCurrent = beatmap.hitObjects[index2]
                        val lastNext = beatmap.hitObjects[index2 + 1]
                        val lastTerm = lastNext.startOffset - lastCurrent.startOffset

                        val plusOD = odTerm * (lastTerm.toDouble() / totalTerm.toDouble())
                        val adaptedTerm = lastTerm.toDouble() + plusOD

                        lastNext.addAttribute("adaptedTerm", adaptedTerm)
                    }

                    lastIndex = index
                    currentTerm = -1
                }
                if(currentTerm >= 0 && currentTerm > term) {
                    currentTerm = term
                }
                if(currentTerm == -1) {
                    currentTerm = term
                }
            }
        }
    }
}