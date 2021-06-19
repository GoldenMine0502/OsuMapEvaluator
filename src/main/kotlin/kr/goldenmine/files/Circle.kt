package kr.goldenmine.files

import kr.goldenmine.util.Point

class Circle(val point: Point, val offset: Int): HitObject {
    override val startOffset: Int
        get() = offset
    override val finishOffset: Int
        get() = offset
    override val startPosition: Point
        get() = point
    override val endPosition: Point
        get() = point

    val attributesPrivate = HashMap<String, Any>()

    override fun getAttributes(): HashMap<String, Any> = attributesPrivate
    override fun addAttribute(key: String, value: Any) {
        attributesPrivate[key] = value
    }
}