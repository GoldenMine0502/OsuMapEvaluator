package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.convertARtoMs
import java.lang.Math.pow
import kotlin.math.*

class AttributeNoteDensity : IAttribute {
    override fun calculateAttribute(beatmap: Beatmap, mods: Int) {
        val ARms = beatmap.convertARtoMs(mods)
        for (index in beatmap.hitObjects.indices) {
            val current = beatmap.hitObjects[index]

            // 현재 노트 기준으로 앞(이전) 뒤(이후) 노트 갯수 구하기
            val amountPlus = beatmap.hitObjects.filter { it.startOffset >= current.startOffset && it.startOffset <= current.startOffset + ARms }.size
            val amountMinus = beatmap.hitObjects.filter { it.startOffset <= current.startOffset && it.startOffset >= current.startOffset - ARms }.size

//            println(amountPlus)
            // TODO 오스에서 이후 나올 노트보다 이전에 나오는 노트가 빨리 화면상에서 사라지므로

            // 점프는 4정도...
            // 연타는 7정도...
            // 이지점프는 8정도...
            // 이지연타는 15정도...
//            val multiplier = log10(9.0 + (min(10 + amountPlus, 10 + amountMinus) / 5.0).pow(2.5))
            current.addAttribute("density", min(amountPlus, amountMinus))
        }

        for(index in beatmap.hitObjects.indices) {
            if(index > 0) {
                val current = beatmap.hitObjects[index]
                val previous = beatmap.hitObjects[index - 1]

                val density = current.getAttribute("density") as Int

                val nearbyNotes =
                    beatmap.hitObjects.filter { it.startOffset >= current.startOffset - ARms && it.startOffset <= current.startOffset + ARms }

                val maxCountPlus =
                    nearbyNotes.maxBy { it.getAttribute("density") as Int }?.getAttribute("density") as Int?

                if (maxCountPlus != null) {
                    val smallCountPlus = //max(
                        density - nearbyNotes.filter { (it.getAttribute("density") as Int) < maxCountPlus }.size

                    val multiplier = log10(10.0 + (smallCountPlus).toDouble().coerceAtLeast(0.1).pow(1.5) * 2)
                    previous.addAttribute("density value", smallCountPlus)
                    previous.addAttribute("density multiplier", multiplier)
                }
            }
        }
    }

    private fun amountRevision(amount: Int): Double {
        return 1.0 / (20.0) * amount * amount
    }

    private fun sigmoid(x: Double): Double {
        return 1.0 / (1 + pow(Math.E, x))
    }
}