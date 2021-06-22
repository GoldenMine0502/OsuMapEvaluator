package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.Slider
import kr.goldenmine.util.Point
import kotlin.math.abs

class AttributeEvaluatorEndPosition: IAttributeEvaluator {
    override fun calculateAttribute(beatmap: Beatmap, mods: Int) {
        for(index in beatmap.hitObjects.indices){
            val current = beatmap.hitObjects[index]
//            beatmap.
            if(current is Slider) {
                val timingPoint = current.timingPoint

//                val bpmMs = timingPoint.bpmMs
                val sliderTerm = current.finishOffset - current.startOffset

                val sliderEndPositionOffset = if(sliderTerm >= 72) 36 else sliderTerm / 2

                val dotOffsets = ArrayList<Int>()

                current.reverseCount.also { reverseCount ->
                    for(i in 1 .. reverseCount) {
                        val arrowOffset = current.startOffset + sliderTerm / reverseCount * i
                        if(arrowOffset < current.finishOffset - sliderEndPositionOffset - 1) {
                            dotOffsets.add(arrowOffset)
                        }
                    }
                }

                timingPoint.bpmMs.also { bpmMs->
                    var tickOffset = current.startOffset + bpmMs

                    while(tickOffset < current.finishOffset - sliderEndPositionOffset - 1) {
                        val tickOffsetInt = tickOffset.toInt()

                        if(!dotOffsets.any { abs(it - tickOffsetInt) <= 2 }) {
                            dotOffsets.add(tickOffset.toInt())
                        }
                        tickOffset += bpmMs
                    }
                }
                dotOffsets.add(current.finishOffset - sliderEndPositionOffset)

                dotOffsets.sort()

//                val finishPosition = current.getSliderPosition(current.finishOffset - sliderEndPositionOffset)
                current.addAttribute("endOffset", sliderEndPositionOffset)
                current.addAttribute("endPosition", dotOffsets.mapNotNull { current.getSliderPosition(it) })
//                current.
                // 슬라이더 크기는 cs d2.4배


            } else {
                current.addAttribute("endOffset", current.finishOffset)
                current.addAttribute("endPosition", listOf(current.startPosition))
            }
        }
    }
}