package kr.goldenmine.files

import kr.goldenmine.util.Point

enum class DotType {
    NONE, CURVE, STRAIGHT
}

class SliderDot(
    val point: Point,
    val dotType: DotType
)