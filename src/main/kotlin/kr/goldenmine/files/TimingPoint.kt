package kr.goldenmine.files

class TimingPoint {
    val offset: Double
    val bpm: Double
    val bpmMs: Double
    val sliderVelocity: Double
    val metronome: Int
    val inherited: Boolean

    val sliderSpeedScore
        get() = bpm * sliderVelocity

    override fun toString(): String {
        return "offset: $offset, bpm: $bpm, sv: $sliderVelocity"
    }

    constructor(offset: Double, bpm: Double, bpmMs: Double, sliderVelocity: Double, metronome: Int, inherited: Boolean) {
        this.offset = offset
        this.bpm = bpm
        this.sliderVelocity = sliderVelocity
        this.metronome = metronome
        this.inherited = inherited
        this.bpmMs = bpmMs
    }

    constructor(line: String, lastbpm: Double?, defaultVelocity: Double) {
        val split = line.split(",")
        offset = split[0].toDouble()
        metronome = split[2].toInt()
        inherited = split[6] == "1"
        bpmMs = split[1].toDouble()
        if (inherited) {
            bpm = lastbpm ?: throw RuntimeException("inherited but lastbpm is null")
            sliderVelocity = defaultVelocity * (-100.0 / split[1].toInt())
        } else {
            bpm = 1.0 / split[1].toDouble() * 1000 * 60
            sliderVelocity = defaultVelocity
        }
    }
}