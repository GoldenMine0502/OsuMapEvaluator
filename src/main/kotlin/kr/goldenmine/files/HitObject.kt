package kr.goldenmine.files

import kr.goldenmine.util.Point

interface HitObject {
    val startOffset: Int
    val finishOffset: Int
    val startPosition: Point
    val endPosition: Point

    fun getAttributes(): HashMap<String, Any>
    fun addAttribute(key: String, value: Any)
//    fun getDeltaTime(other: Int): Int
//    fun getStrainTime(other: Int) = getDeltaTime(other).coerceAtLeast(50)
}