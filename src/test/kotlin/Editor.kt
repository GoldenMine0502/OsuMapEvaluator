import kr.goldenmine.BeatmapPreviewer
import kr.goldenmine.files.loadBeatmap
import java.io.File
import javax.swing.JFrame

fun main() {
    val beatmapPreviewer = BeatmapPreviewer(loadBeatmap(File("testmaps/Zutto Mayonaka de Ii no ni. - Humanoid (Meg) [Collab].osu")))
    beatmapPreviewer.isVisible = true
    beatmapPreviewer.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
}