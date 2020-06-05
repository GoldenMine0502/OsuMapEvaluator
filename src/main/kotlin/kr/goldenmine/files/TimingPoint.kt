package kr.goldenmine.files

class TimingPoint {
    val offset: Double
    val bpm: Double
    val sliderVelocity: Double
    val metronome: Int
    val inherited: Boolean

    val sliderSpeedScore
        get() = bpm * sliderVelocity

    constructor(offset: Double, bpm: Double, sliderVelocity: Double, metronome: Int, inherited: Boolean) {
        this.offset = offset
        this.bpm = bpm
        this.sliderVelocity = sliderVelocity
        this.metronome = metronome
        this.inherited = inherited
    }

    constructor(line: String, lastbpm: Double?, defaultVelocity: Double) {
        val split = line.split(",")
        offset = split[0].toDouble()
        metronome = split[2].toInt()
        inherited = split[6] == "1"
        if (inherited) {
            bpm = lastbpm ?: throw RuntimeException("inherited but lastbpm is null")
            sliderVelocity = defaultVelocity * (-100.0 / split[1].toInt())
        } else {
            bpm = 1.0 / split[1].toDouble() * 1000 * 60
            sliderVelocity = defaultVelocity
        }
    }
}