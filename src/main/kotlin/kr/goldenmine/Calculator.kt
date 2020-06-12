package kr.goldenmine

import kr.goldenmine.evaluate.evaluator.*
import kr.goldenmine.files.Beatmap
import kr.goldenmine.files.loadBeatmap
import kr.goldenmine.util.Mods
import kr.goldenmine.util.getResource
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun main() {
    val chrome = getResource("chromedriver.exe")

    if (chrome != null && chrome.exists()) {
        System.setProperty("webdriver.chrome.driver", chrome.absolutePath)
        //System.setProperty("webdriver.gecko.driver", "resources/geckodriver.exe");
    } else {
        println("chromedriver가 존재하지 않습니다. 비트맵 다운로드가 불가능합니다.")
    }

    val evaluators = ArrayList<BeatmapEvaluator>()
    evaluators.add(EvaluatorBpm())
    evaluators.add(EvaluatorRealbpm())
    evaluators.add(EvaluatorObtusePercent())
    evaluators.add(EvaluatorObtusePercentWithDistance())
    evaluators.add(EvaluatorDistanceVarience())
    //evaluators.add(EvaluatorDistanceVarienceMultiply())
    evaluators.add(EvaluatorVelocity())

    val scanner = Scanner(System.`in`)

    print("osu file(not osz) route: ")
    val folder = File(scanner.nextLine())
    print("osu file(not osz) route to preview: ")
    val route = File(scanner.nextLine())
//    val route = File("testmaps/SYU (from GALNERYUS) - REASON (BarkingMadDog) [A THOUSAND SWORDS].osu")

    print("mods(ex.DTHR, none): ")
    val modsText = scanner.nextLine().toUpperCase()
//    val modsText = "none"
//
//    print("osu id: ")
//    val osuId = scanner.nextLine()
//
//    print("osu pw: ")
//    val osuPw = scanner.nextLine()

    var mods = 0
    if(modsText.contains("DT")) mods = mods or Mods.DT.value
    if(modsText.contains("EZ")) mods = mods or Mods.EZ.value
    if(modsText.contains("HT")) mods = mods or Mods.HT.value
    if(modsText.contains("HR")) mods = mods or Mods.HR.value

    // calculator
    val beatmaps = loadAllBeatmaps(folder)

    println("${beatmaps.size} beatmaps loaded")

    val workbook = HSSFWorkbook()
    val sheet = workbook.createSheet()

    // 초기 표 타이틀 설정
    sheet.createRow(0).also { row ->
        row.createCell(0).setCellValue("id")
        row.createCell(1).setCellValue("setId")
        row.createCell(2).setCellValue("name")
        row.createCell(3).setCellValue("version")

        repeat(evaluators.size) {
           row.createCell(it + 4).setCellValue(evaluators[it].type)
        }
    }

    // 값 넣기
    repeat(beatmaps.size) { beatmapIndex ->
        val beatmap = beatmaps[beatmapIndex]
        sheet.createRow(1 + beatmapIndex).also {row ->
            row.createCell(0).setCellValue(beatmap.beatmapId.toDouble())
            row.createCell(1).setCellValue(beatmap.beatmapSetId.toDouble())
            row.createCell(2).setCellValue(beatmap.title)
            row.createCell(3).setCellValue(beatmap.version)

            repeat(evaluators.size) {
                row.createCell(it + 4).setCellValue(evaluators[it].evaluate(beatmap, mods).toString())
            }
        }
    }

    val resultFile = File("result_${folder.path}.xls")
    if(!resultFile.exists()) resultFile.createNewFile()

    val outputStream = FileOutputStream(resultFile)

    workbook.write(outputStream)
    outputStream.flush()
    outputStream.close()

    // previewer
    if(route.exists()) {
        val beatmap = loadBeatmap(route)

        for(evaluator in evaluators) {
            println("${evaluator.type}: ${evaluator.evaluate(beatmap, mods)}")
        }

        val beatmapPreviewer = BeatmapPreviewer(beatmap)
        beatmapPreviewer.startRender()
    } else {
        println("the file does not exist")
    }

}

fun loadAllBeatmaps(folder: File): List<Beatmap> {
    return folder
        .listFiles()
        ?.filter { it.name.endsWith(".osu") }
        ?.map { loadBeatmap(it) }
        ?.toList()
        ?: ArrayList()
}