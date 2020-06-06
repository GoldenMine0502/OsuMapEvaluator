package kr.goldenmine.files

import kr.goldenmine.util.Point

val spinnerPos = Point(256, 192)

data class Spinner(
    override val startOffset: Int,
    override val finishOffset: Int
): HitObject {
    override val startPosition: Point
        get() = spinnerPos
    override val endPosition: Point
        get() = spinnerPos
}