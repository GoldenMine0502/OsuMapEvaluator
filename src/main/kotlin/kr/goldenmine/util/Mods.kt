package kr.goldenmine.util

val None = 0
val HR = 2 shl 0
val DT = 2 shl 1

enum class Mods(val value: Int) {
    NONE(0),
    HR(1), // x1.4
    DT(1 shl 1), //
    EZ(1 shl 2), // x0.5
    HT(1 shl 3), // /1.4
}

fun calculateODtoMillis(base: Double, mods: Int): Double {
    var base = base
    if((mods and Mods.HR.value) > 0) {
        base *= 1.4
        if(base > 10) base = 10.0
    }
    if((mods and Mods.EZ.value) > 0) {
        base *= 0.5
        if(base < 0) base = 0.0
    }

    var ms = 79.5 - base * 6.0
    if((mods and Mods.DT.value) > 0) {
        ms /= 3.0
        ms *= 2.0
    }
    if((mods and Mods.HT.value) > 0) {
        ms /= 3
        ms *= 4.0
    }

    return ms
}