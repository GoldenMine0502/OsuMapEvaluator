package kr.goldenmine.files

import kr.goldenmine.util.Mods
import java.io.File

class Beatmap(
    val title: String,
    val titleUnicode: String,
    val artist: String,
    val artistUnicode: String,
//    val creator: String
    val version: String,
//    val source: String
//    val tags: List<String>
    val beatmapId: Int,
    val beatmapSetId: Int,
    val HP: Double,
    val CS: Double,
    val OD: Double,
    val AR: Double,
    val baseSliderVelocity: Double,
    //val sliderTickRate: Int,
    val timingPoints: List<TimingPoint>,
    val hitObjects: List<HitObject>
) {

}

const val lastOffset = 10000

val Beatmap.length
    get() = hitObjects.last().finishOffset + lastOffset

fun Beatmap.convertARtoMs(mods: Int = 0): Double {
    var ARMods = AR

    if(mods and Mods.HR.value > 0) {
        ARMods *= 1.4
        if(ARMods > 10) ARMods = 10.0
    }
    if(mods and Mods.EZ.value > 0) {
        ARMods *= 0.5
    }

    var ms = when {
        ARMods < 5 -> 1200.0 + 600.0 * (5 - ARMods) / 5
        ARMods == 5.0 -> 1200.0
        else -> 1200.0 - 750.0 * (ARMods - 5) / 5
    }

    if(mods and Mods.DT.value > 0) {
        ms *= 2.0 / 3.0
    }
    if(mods and Mods.HT.value > 0) {
        ms *= 4.0 / 3.0
    }

    return ms
}

fun Beatmap.convertCStoRadius(mods: Int = 0): Double {
    var multiplier = 1.0
    if(mods and Mods.HR.value > 0)
        multiplier *= 1.4
    if(mods and Mods.EZ.value > 0)
        multiplier *= 0.5
//        if(mods and Mods.HR.value > 0)
    return 54.4 - 4.48 * CS * multiplier
}

fun Beatmap.calculateODPre(mods: Int): Double {
    var ODMods = OD

    if(mods and Mods.HR.value > 0) {
        ODMods *= 1.4
        if(ODMods > 10) ODMods = 10.0
    }
    if(mods and Mods.EZ.value > 0) {
        ODMods *= 0.5
    }

    return ODMods
}

private fun Beatmap.calculateODPost(ms: Double, mods: Int): Double {
    var ms = ms
    if(mods and Mods.DT.value > 0) {
        ms *= 2.0 / 3.0
    }
    if(mods and Mods.HT.value > 0) {
        ms *= 4.0 / 3.0
    }

    return ms
}
/*
50	400ms - 20ms * OD
100	280ms - 16ms * OD
300	160ms - 12ms * OD
 */
fun Beatmap.convertODto300(mods: Int = 0): Double {
    return calculateODPost(160 - 12 * calculateODPre(mods), mods)
}
fun Beatmap.convertODto100(mods: Int = 0): Double {
    return calculateODPost(280 - 16 * calculateODPre(mods), mods)
}
fun Beatmap.convertODto50(mods: Int = 0): Double {
    return calculateODPost(400 - 20 * calculateODPre(mods), mods)
}