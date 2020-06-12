package kr.goldenmine.files

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