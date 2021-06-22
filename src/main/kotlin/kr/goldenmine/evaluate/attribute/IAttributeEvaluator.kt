package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap

interface IAttributeEvaluator {
    fun calculateAttribute(beatmap: Beatmap, mods: Int)
}