package kr.goldenmine.files

import kr.goldenmine.util.Point

class Slider(
    val points: List<SliderDot>,
    override val startOffset: Int,
    override val finishOffset: Int,
    val type: Type
): HitObject {

    enum class Type(val chracter: String) {
        STRAIGHT("L"), CURVE("P"), BEZIER("B")
    }
    override val startPosition: Point
        get() = points.first().point
    override val endPosition: Point
        get() = points.last().point
}