package kr.goldenmine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.goldenmine.evaluate.attribute.*
import kr.goldenmine.evaluate.circle.*
import kr.goldenmine.files.*
import kr.goldenmine.util.Point
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSlider
import kotlin.math.abs
import kotlin.math.round

class BeatmapPreviewer(private val beatmap: Beatmap, private val mods: Int = 0) : JFrame("osu! previewer") {
    val frameWidth = 1366
    val frameHeight = 768
    val realWidth = frameHeight / 3 * 4
    val circleSize = round(beatmap.convertCStoRadius(mods) / 640.0 * frameWidth * 2).toInt()
    val titleGap = 17
    val sliderGap = 17
    val slider: JSlider

    val buffer: BufferedImage

    val drawPanel = JPanel()

    var lastRenderedHitObjects: List<HitObject>? = null
    var selectedHitObjectIndex = -1

    val attributors = ArrayList<IAttributeEvaluator>()
    val evaluators = ArrayList<CircleEvaluator>()

    init {
        drawPanel.preferredSize = Dimension(frameWidth + circleSize, frameHeight + circleSize + titleGap + sliderGap)
        slider = JSlider(0, beatmap.length, 0)
        buffer =
            BufferedImage(drawPanel.preferredSize.width, drawPanel.preferredSize.height, BufferedImage.TYPE_INT_ARGB)

        size = drawPanel.preferredSize

        layout = BorderLayout()

        add(drawPanel)
        add(slider, BorderLayout.SOUTH)

        initEvaluators()
        registerEvents()
    }

    fun initEvaluators() {
        attributors.add(AttributeEvaluatorEndPosition())
        attributors.add(AttributeEvaluatorJump2())
        attributors.add(AttributeEvaluatorJumpVariance())
        attributors.add(AttributeEvaluatorNoteDensity())
        attributors.add(AttributeEvaluatorAdaptedTerm())

        evaluators.add(CircleEvaluatorJump())
        evaluators.add(CircleEvaluatorDistance())
        evaluators.add(CircleEvaluatorTerm())
        evaluators.add(CircleEvaluatorJump2())
        evaluators.add(CircleEvaluatorDistance2())
        evaluators.add(CircleEvaluatorVariance())
        evaluators.add(CircleEvaluatorDensity())
        evaluators.add(CircleEvaluatorDensityMultiplier())
        evaluators.add(CircleEvaluatorJumpTest())
        evaluators.add(CircleEvaluatorAdaptedTerm())

        attributors.forEach { it.calculateAttribute(beatmap, mods) }
    }

    fun registerEvents() {
        slider.addChangeListener {
            selectedHitObjectIndex = -1

            val ms = slider.value.toLong()
            val toRenderHitObjects = searchCircles(ms)

            renderFrame(toRenderHitObjects)

            lastRenderedHitObjects = toRenderHitObjects
        }

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val point = Point(adaptPosXReversed(e.x), adaptPosYReversed(e.y) - titleGap)
                val ms = slider.value.toLong()
                val msAR = beatmap.convertARtoMs(mods)

                for (index in beatmap.hitObjects.indices.reversed()) {
                    val hitObject = beatmap.hitObjects[index]

                    if (abs(hitObject.startOffset - ms) <= msAR) {
//                        println("${hitObject.startPosition} ${point}")
                        if ((hitObject.startPosition - point).length <= circleSize / 4) {
                            selectedHitObjectIndex = index
                            break
                        }
                    }
                }
                lastRenderedHitObjects?.run {
                    renderFrame(this)
                }
            }
        })
    }

    fun startRender() {
        isVisible = true

        val start = System.currentTimeMillis()
        var renderedCount = 0
        var renderingCount = 0
        val hitObjects = beatmap.hitObjects
        val toRender = LinkedList<HitObject>()

        GlobalScope.launch(Dispatchers.IO) {
            while (renderedCount < hitObjects.size) {
                val elapsedMs = System.currentTimeMillis() - start
                if (renderingCount < hitObjects.size && (hitObjects[renderingCount].startOffset - elapsedMs) <= (1950 - 150 * beatmap.AR) * 4) {
                    toRender.add(hitObjects[renderingCount])

                    renderingCount++
                }
                if (toRender.size > 0) {
                    if (elapsedMs >= toRender.first().finishOffset) {
                        toRender.removeFirst()
                        renderedCount++
                    }
                }
                renderFrame(toRender)

                Thread.sleep(10L)
            }
        }
    }

    fun searchCircles(elapsedMs: Long): List<HitObject> {
        val msAR = beatmap.convertARtoMs(mods)
        return beatmap.hitObjects.filter { abs(it.startOffset - elapsedMs) <= msAR }
    }

    fun adaptPosX(x: Int) = (x / 640.0 * frameWidth).toInt() + (frameWidth - realWidth) / 4 + circleSize / 2
    fun adaptPosY(y: Int) = (y / 360.0 * frameHeight).toInt() + circleSize / 2

    fun adaptPosXReversed(x: Int) = ((x - (frameWidth - realWidth) / 4 - circleSize / 2) * 640.0 / frameWidth).toInt()
    fun adaptPosYReversed(y: Int) = ((y - circleSize / 2) * 360.0 / frameHeight).toInt()

    fun renderFrame(toRender: List<HitObject>) {
        val graphics = buffer.graphics as Graphics2D
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, buffer.width, buffer.height)

        graphics.color = Color.BLACK
        toRender.forEach { hitObject ->

            fun drawCircle(pos: Point, size: Int) {
                //graphics.fillOval()
                val previousColor = graphics.color

                if (selectedHitObjectIndex >= 0
                    && beatmap.hitObjects[selectedHitObjectIndex] == hitObject
                )
                    graphics.color = Color.GREEN

                if (selectedHitObjectIndex >= 1
                    && beatmap.hitObjects[selectedHitObjectIndex - 1] == hitObject
                )
                    graphics.color = Color.CYAN

                if (selectedHitObjectIndex >= 0
                    && selectedHitObjectIndex < beatmap.hitObjects.size - 1
                    && beatmap.hitObjects[selectedHitObjectIndex + 1] == hitObject
                )
                    graphics.color = Color.BLUE

//                if(selectedHitObjectIndex >= 0) {
//                    val multiplier = hitObject.getAttribute("density multiplier") as Double
//                    graphics.drawString(multiplier.toString(), adaptPosX(pos.xInt), adaptPosY(pos.yInt))
//                }

                graphics.drawArc(
                    (adaptPosX(pos.xInt) - (circleSize) / 2 + abs(circleSize - size) / 2).toInt(),
                    (adaptPosY(pos.yInt) - (circleSize) / 2 + abs(circleSize - size) / 2).toInt(),
                    size,
                    size,
                    0,
                    360
                )

                graphics.color = previousColor
            }

            fun drawColoredCircle(pos: Point, size: Int) {
                val previousColor = graphics.color
                graphics.color = Color.GREEN
                drawCircle(pos, size)

                graphics.color = previousColor
            }

            if (hitObject is Circle) {
                drawCircle(hitObject.point, circleSize)
            }
            if (hitObject is Slider) {
                drawCircle(hitObject.startPosition, circleSize)
                drawCircle(hitObject.startPosition, circleSize / 2)
                drawCircle(hitObject.endPosition, circleSize)

                for (i in 0 until hitObject.path.size - 1) {
                    val currentPoint = hitObject.path[i]
                    val nextPoint = hitObject.path[i + 1]

                    graphics.drawLine(
                        adaptPosX(currentPoint.xInt).toInt(),
                        adaptPosY(currentPoint.yInt).toInt(),
                        adaptPosX(nextPoint.xInt).toInt(),
                        adaptPosY(nextPoint.yInt).toInt()
                    )
                }
            }
            if (hitObject is Spinner) {
                graphics.drawString("spinner is arriving", size.width / 2, size.height / 2)
            }
            //repaint()
        }

        toRender.forEach {
            //            if() {
            fun drawRect(pos: Point) {
                graphics.drawRect(adaptPosX(pos.x.toInt()) - 2, adaptPosY(pos.y.toInt()) - 2, 4, 4)
            }

            val pos = it.getAttributes()["jump2pos"]
            val pos2 = it.getAttributes()["jump2pos2"]

            if (pos != null && pos2 != null) {
                drawRect(pos as Point)
                drawRect(pos2 as Point)
                graphics.drawLine(
                    adaptPosX(pos.x.toInt()),
                    adaptPosY(pos.y.toInt()),
                    adaptPosX(pos2.x.toInt()),
                    adaptPosY(pos2.y.toInt())
                )
            }


            if (it is Slider) {
                (it.getAttribute("endPosition") as List<Point>?)?.run {
                    if (size >= 2)
                        println("$size ${it.startOffset} $this")
                    this
                }?.forEach { endPosition ->
                    val x = adaptPosX(endPosition.xInt)
                    val y = adaptPosY(endPosition.yInt)
                    graphics.fillOval(x - 4, y - 4, 8, 8)
//                    graphics.drawString(endPosition.toString(), adaptPosX(endPosition.xInt), adaptPosY(endPosition.yInt))
//                        graphics.drawString(it.finishOffset.toString(), adaptPosX(endPosition.xInt), adaptPosY(endPosition.yInt) + 10)
//                        graphics.drawString(it.startOffset.toString(), adaptPosX(endPosition.xInt), adaptPosY(endPosition.yInt) + 20)
                }
            }
        }
//                val endPosition = it.getAttribute("endPosition") as List<Point>
//                graphics.drawString(endPosition.toString(), adaptPosX(endPosition.xInt), adaptPosY(endPosition.yInt))
//                graphics.drawString(it.finishOffset.toString(), adaptPosX(endPosition.xInt), adaptPosY(endPosition.yInt) + 10)
//                graphics.drawString(it.startOffset.toString(), adaptPosX(endPosition.xInt), adaptPosY(endPosition.yInt) + 20)
        if (selectedHitObjectIndex >= 0) {
//            val builderLast = StringBuilder()
//            val builderNext = StringBuilder()

            var lines = 0
            evaluators.forEach {
                val result = it.evaluate(beatmap, selectedHitObjectIndex, 0)
                graphics.drawString("${it.type}: ${result.lastResult}\n", 5, 20 + lines * 10)
                graphics.drawString("${it.type}: ${result.nextResult}\n", 5, 200 + lines * 10)
                lines++
//                builderLast.append("${it.type}: ${result.lastResult}\n")
//                builderNext.append("${it.type}: ${result.nextResult}\n")
            }

        }

        drawPanel.graphics.drawImage(buffer, 0, 0, null)
    }


}