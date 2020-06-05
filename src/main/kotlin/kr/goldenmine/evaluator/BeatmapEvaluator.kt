package kr.goldenmine.evaluator

import kr.goldenmine.files.Beatmap

interface BeatmapEvaluator {
    val type: String

    fun evaluate(beatmap: Beatmap, mods: Int): Any?
}