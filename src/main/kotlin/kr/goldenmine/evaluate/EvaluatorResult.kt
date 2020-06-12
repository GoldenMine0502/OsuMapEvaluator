package kr.goldenmine.evaluate

import kr.goldenmine.evaluate.evaluator.BeatmapEvaluator
import kr.goldenmine.files.Beatmap

class EvaluatorResult(val beatmap: Beatmap, val mods: Int, val evaluators: List<BeatmapEvaluator>) {
    val results = HashMap<String, String>()

    fun calculateResult() {
        evaluators.forEach {
            results[it.type] = it.evaluate(beatmap, mods).toString()
        }
    }
}