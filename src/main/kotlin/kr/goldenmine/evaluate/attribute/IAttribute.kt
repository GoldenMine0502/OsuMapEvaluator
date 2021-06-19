package kr.goldenmine.evaluate.attribute

import kr.goldenmine.files.Beatmap

interface IAttribute {
    fun calculateAttribute(beatmap: Beatmap, mods: Int)
}