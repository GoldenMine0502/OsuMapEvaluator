package kr.goldenmine

import kr.goldenmine.evaluator.*
import kr.goldenmine.files.loadBeatmap
import kr.goldenmine.util.Mods
import java.io.File
import java.util.*

fun main() {
    val evaluators = ArrayList<BeatmapEvaluator>()
    evaluators.add(EvaluatorBpm())
    evaluators.add(EvaluatorRealbpm())
    evaluators.add(EvaluatorObtusePercent())
    evaluators.add(EvaluatorObtusePercentWithDistance())

    val scanner = Scanner(System.`in`)

    print("osu file(not osz) route: ")
    val route = File(scanner.nextLine())
//    val route = File("testmaps/SYU (from GALNERYUS) - REASON (BarkingMadDog) [A THOUSAND SWORDS].osu")

    print("mods(ex.DTHR, none): ")
    val modsText = scanner.nextLine().toUpperCase()
//    val modsText = "none"

    var mods = 0
    if(modsText.contains("DT")) mods = mods or Mods.DT.value
    if(modsText.contains("EZ")) mods = mods or Mods.EZ.value
    if(modsText.contains("HT")) mods = mods or Mods.HT.value
    if(modsText.contains("HR")) mods = mods or Mods.HR.value

    if(route.exists()) {
        val beatmap = loadBeatmap(route)

        for(evaluator in evaluators) {
            println("${evaluator.type}: ${evaluator.evaluate(beatmap, mods)}")
        }
    } else {
        println("the file does not exist")
    }
}