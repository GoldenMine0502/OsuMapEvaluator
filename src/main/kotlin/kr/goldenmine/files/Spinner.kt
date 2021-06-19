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

    val attributesPrivate = HashMap<String, Any>()

    override fun getAttributes(): HashMap<String, Any> = attributesPrivate
    override fun addAttribute(key: String, value: Any) {
        attributesPrivate[key] = value
    }

    override fun getAttribute(key: String): Any? {
        return attributesPrivate[key]
    }
}