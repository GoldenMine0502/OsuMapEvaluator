package kr.goldenmine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.goldenmine.evaluate.circle.CircleEvaluatorJump
import kr.goldenmine.evaluate.circle.CircleEvaluator
import kr.goldenmine.evaluate.circle.CircleEvaluatorDistance
import kr.goldenmine.evaluate.circle.CircleEvaluatorTerm
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

class BeatmapPreviewer(private val beatmap: Beatmap): JFrame("osu! previewer") {
    val circleSize = 45
    val titleGap = 38
    val slider: JSlider

    val buffer: BufferedImage

    val drawPanel = JPanel()

    var lastRenderedHitObjects: List<HitObject>? = null

    var selectedHitObjectIndex = -1

    val evaluators = ArrayList<CircleEvaluator>()

    init {
        size = Dimension(512 + circleSize * 3, 372 + circleSize * 3 + titleGap)
        slider = JSlider(0, beatmap.length, 0)
        buffer = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB)

        layout = BorderLayout()

        add(drawPanel)
        add(slider, BorderLayout.SOUTH)

        evaluators.add(CircleEvaluatorJump())
        evaluators.add(CircleEvaluatorDistance())
        evaluators.add(CircleEvaluatorTerm())

        registerEvents()
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
                val point = Point(e.x, e.y - titleGap)
                val ms = slider.value.toLong()
                val msAR = beatmap.convertARtoMs()

                for(index in beatmap.hitObjects.indices.reversed()) {
                    val hitObject = beatmap.hitObjects[index]

                    if (abs(hitObject.startOffset - ms) <= msAR) {
//                        println("${hitObject.startPosition} ${point}")
                        if ((hitObject.startPosition - point).length <= circleSize) {
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
            while(renderedCount < hitObjects.size) {
                val elapsedMs = System.currentTimeMillis() - start
                if(renderingCount < hitObjects.size && (hitObjects[renderingCount].startOffset - elapsedMs) <= 1950 - 150 * beatmap.AR) {
                    toRender.add(hitObjects[renderingCount])

                    renderingCount++
                }
                if(toRender.size > 0) {
                    if(elapsedMs >= toRender.first().finishOffset) {
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
        val msAR = beatmap.convertARtoMs()
        return beatmap.hitObjects.filter { abs(it.startOffset - elapsedMs) <= msAR }
    }

    fun renderFrame(toRender: List<HitObject>) {
        val graphics = buffer.graphics as Graphics2D
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, buffer.width, buffer.height)

        graphics.color = Color.BLACK
        toRender.forEach { hitObject ->
            fun drawCircle(pos: Point, size: Int) {
                //graphics.fillOval()
                graphics.drawArc(
                    (pos.xInt - (circleSize) / 2 + (circleSize - size) / 2),
                    (pos.yInt - (circleSize) / 2 + (circleSize - size) / 2),
                    size,
                    size,
                    0,
                    360)
            }

            if(hitObject is Circle) {
                drawCircle(hitObject.point, circleSize)
            }
            if(hitObject is Slider) {
                drawCircle(hitObject.startPosition, circleSize)
                drawCircle(hitObject.startPosition, circleSize / 2)
                drawCircle(hitObject.endPosition, circleSize)

                for(i in 0 until hitObject.path.size - 1) {
                    val currentPoint = hitObject.path[i]
                    val nextPoint = hitObject.path[i + 1]

                    graphics.drawLine(
                        (currentPoint.xInt).toInt(),
                        (currentPoint.yInt).toInt(),
                        (nextPoint.xInt).toInt(),
                        (nextPoint.yInt).toInt()
                    )
                }
            }
            if(hitObject is Spinner) {
                graphics.drawString("spinner is arriving", size.width / 2, size.height / 2)
            }
            //repaint()
        }

        if(selectedHitObjectIndex >= 0) {
//            val builderLast = StringBuilder()
//            val builderNext = StringBuilder()

            var lines = 0
            evaluators.forEach {
                val result = it.evaluate(beatmap, selectedHitObjectIndex, 0)

                graphics.drawString("${it.type}: ${result.lastResult}\n", 5, 20 + lines * 10)
                graphics.drawString("${it.type}: ${result.nextResult}\n", 5, 120 + lines * 10)
                lines++
//                builderLast.append("${it.type}: ${result.lastResult}\n")
//                builderNext.append("${it.type}: ${result.nextResult}\n")
            }

        }

        drawPanel.graphics.drawImage(buffer, 0, 0, null)
    }




}