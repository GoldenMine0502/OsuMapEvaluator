package kr.goldenmine.aimvalue

import kr.goldenmine.files.Circle
import kr.goldenmine.util.Point
import kr.goldenmine.util.toDegrees
import kr.goldenmine.util.toRadians
import kotlin.math.*

val angleBonusBegin = (Math.PI / 3).toDegrees()
const val scale = 90
const val timingThreshold = 107
const val strainDecayBase = 0.15
const val difficultyMulitiplier = 0.0675
const val skillMultiplier = 26.25
const val decayWeight = 0.9
const val sectionLength = 400

fun main() {
    println("=== Normal ===")

    val normalCircles = ArrayList<Circle>()
    normalCircles.add(
        Circle(
            Point(
                323,
                332
            ), 33930
        )
    )
    normalCircles.add(
        Circle(
            Point(
                397,
                286
            ), 34253
        )
    )
    normalCircles.add(
        Circle(
            Point(
                436,
                206
            ), 34576
        )
    ) // circle
    normalCircles.add(
        Circle(
            Point(
                432,
                119
            ), 34898
        )
    )

    val aimNormal =
        calculateAimValue(
            magnifyCircles(
                normalCircles
            )
        )

    println("aimValue: $aimNormal")
    println("star: ${calculateStarRating(aimNormal, aimNormal)}")

    println("=== Insane ===")

    val insaneCircles = ArrayList<Circle>()
    insaneCircles.add(
        Circle(
            Point(
                49,
                60
            ), 82317
        )
    )
    insaneCircles.add(
        Circle(
            Point(
                209,
                114
            ), 82478
        )
    )
    insaneCircles.add(
        Circle(
            Point(
                115,
                165
            ), 82640
        )
    )
    insaneCircles.add(
        Circle(
            Point(
                170,
                27
            ), 82801
        )
    )

    val aimInsane =
        calculateAimValue(
            magnifyCircles(
                insaneCircles
            )
        )

    println("aimValue: $aimInsane")
    println("star: ${calculateStarRating(aimInsane, aimInsane)}")

    println("=== Extreme ===")

    val extremeCircles = ArrayList<Circle>()
    extremeCircles.add(
        Circle(
            Point(
                122,
                123
            ), 173380
        )
    )
    extremeCircles.add(
        Circle(
            Point(
                336,
                296
            ), 173500
        )
    )
    extremeCircles.add(
        Circle(
            Point(
                335,
                47
            ), 173620
        )
    )
    extremeCircles.add(
        Circle(
            Point(
                184,
                297
            ), 173740
        )
    )

    val aimExtreme = calculateAimValue(
        magnifyCircles(extremeCircles)
    )

    println("aimValue: $aimExtreme")
    println("star: ${calculateStarRating(aimExtreme, aimExtreme)}")
}

fun calculateStarRating(aimValue: Double, speedValue: Double): Double = aimValue + speedValue + sqrt(aimValue * speedValue) / 2

fun calculateAimValue(circles: List<Circle>): Double {
    val hitObjects = createHitObjectsFromCircles(circles)

    val skill = AimSkill()
    var currentSectionEnd = hitObjects.first().currentPoint.offset

    for(hitObject in hitObjects) {
        while(hitObject.currentPoint.offset > currentSectionEnd) {
            skill.saveCurrentPeak()
            skill.startNewSectionFrom(currentSectionEnd)

            currentSectionEnd += sectionLength
        }

        skill.process(hitObject)
    }

    skill.saveCurrentPeak()

    val aimRating = sqrt(skill.difficultyValue()) * difficultyMulitiplier


    return aimRating
}

fun magnifyCircles(list: List<Circle>): List<Circle> {
    val result = ArrayList<Circle>()
    val first = list[0]
    val last = list[1]

    val deltaTime = (last.offset - first.offset) * 2

    repeat(49) {
        val newFirst = Circle(first.point, first.offset + deltaTime * it)
        val newSecond = Circle(last.point, last.offset + deltaTime * it)
        result.add(newFirst)
        result.add(newSecond)
    }

    return result
}

fun createHitObjectsFromCircles(list: List<Circle>): List<AdvancedCircle> {
    val result = ArrayList<AdvancedCircle>()
    for(i in list.indices) {
        val previous = i - 1
        val current = i
        val next = i + 1
        if(previous >= 0 && next < list.size) {
            result.add(
                AdvancedCircle(
                    list[previous],
                    list[current],
                    list[next]
                )
            )
        }
    }

    return result
}

class AimSkill {
    val strainPeaks = ArrayList<Double>()
    var currentSectionPeak = 1.0
    var currentStrain = 1.0

    var previousHitObject: AdvancedCircle? = null

    fun saveCurrentPeak() {
        if(previousHitObject != null)
            strainPeaks.add(currentSectionPeak)
    }

    fun startNewSectionFrom(offset: Int) {
        if(previousHitObject != null)
            currentSectionPeak = currentStrain * (offset - previousHitObject!!.currentPoint.offset).strainDecay()
    }

    fun process(hitObject: AdvancedCircle) {
        currentStrain *= hitObject.deltaTime.strainDecay()

        val aimStrain = calculateAimStrain(previousHitObject, hitObject).result

        //println(aimStrain)

        currentStrain += aimStrain * skillMultiplier

        currentSectionPeak = max(currentStrain, currentSectionPeak)

        previousHitObject = hitObject
    }

    fun difficultyValue(): Double {
        var difficulty = 0.0
        var weight = 1.0

        println(strainPeaks.joinToString())

        //list.sortDescending()

        strainPeaks.sortedByDescending { it }.forEach {
            difficulty += it * weight
            weight *= decayWeight
            //println("diff: $difficulty $it weight: $weight")
        }

        return difficulty
    }
}

fun calculateAimStrain(lastObject: AdvancedCircle?, currentObject: AdvancedCircle): AimValueResult {
    val result = if(lastObject != null && currentObject.angle > angleBonusBegin) {
        val lastScaledDistance = lastObject.jumpDistance.coerceAtLeast(0.0)
        val currentScaledDistance = currentObject.jumpDistance.coerceAtLeast(0.0)

        val angleBonus = sqrt(lastScaledDistance * sin((currentObject.angle - angleBonusBegin).toRadians()).pow(2) * currentScaledDistance)
        1.5 * ((max(angleBonus, 0.0)) / max(timingThreshold, lastObject.strainTime)).applyDiminishingExp()
    } else {
        0.0
    }

    val jumpDistanceExp = currentObject.jumpDistance

    val objectValue = max(result + jumpDistanceExp / max(currentObject.strainTime,
        timingThreshold
    ), jumpDistanceExp / currentObject.strainTime)

    return AimValueResult(result, objectValue)
}

data class AimValueResult(val angleBonus: Double, val result: Double)

fun Double.applyDiminishingExp(): Double = this.pow(0.99)
fun Double.strainDecay(): Double = strainDecayBase.pow(this / 1000.0)
fun Int.strainDecay(): Double = strainDecayBase.pow(this / 1000.0)