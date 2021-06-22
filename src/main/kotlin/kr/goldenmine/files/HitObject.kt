package kr.goldenmine.files

import kr.goldenmine.util.Point

interface HitObject: IAttribute {
    val startOffset: Int
    val finishOffset: Int
    val startPosition: Point
    val endPosition: Point


//    fun getDeltaTime(other: Int): Int
//    fun getStrainTime(other: Int) = getDeltaTime(other).coerceAtLeast(50)
}