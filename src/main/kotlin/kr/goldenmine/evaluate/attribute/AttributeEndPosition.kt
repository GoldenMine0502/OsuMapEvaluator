package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.Slider
import kr.goldenmine.util.Point

class AttributeEndPosition: IAttribute {
    override fun calculateAttribute(beatmap: Beatmap, mods: Int) {
        for(index in beatmap.hitObjects.indices){
            val current = beatmap.hitObjects[index]
            if(current is Slider) {
                val sliderTerm = current.finishOffset - current.startOffset

                val sliderEndPositionOffset = if(sliderTerm >= 72) 36 else sliderTerm / 2

                val finishPosition = current.sliderPosition(current.finishOffset - sliderEndPositionOffset)
                current.addAttribute("endPosition", finishPosition ?: Point(-5, -5))
//                current.
                // 슬라이더 크기는 cs d2.4배

            } else {
                current.addAttribute("endPosition", current.startPosition)
            }
        }
    }
}