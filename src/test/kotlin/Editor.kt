import kr.goldenmine.BeatmapPreviewer
import kr.goldenmine.files.loadBeatmap
import kr.goldenmine.util.Mods
import java.io.File
import javax.swing.JFrame

fun main() {
    val mods = Mods.NONE.value or Mods.EZ.value
    val beatmapPreviewer = BeatmapPreviewer(loadBeatmap(File("testmaps/SYU (from GALNERYUS) - REASON (BarkingMadDog) [A THOUSAND SWORDS].osu")), mods)
    beatmapPreviewer.isVisible = true
    beatmapPreviewer.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
}