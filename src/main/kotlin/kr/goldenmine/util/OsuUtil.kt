package kr.goldenmine.util


fun calculateBPM(ms: Double): Double {
    return 1.0 / ms * 1000 * 60
}