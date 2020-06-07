package kr.goldenmine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.goldenmine.files.*
import kr.goldenmine.util.Point
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel

class BeatmapPreviewer(private val beatmap: Beatmap): JFrame("osu! previewer") {
    val circleSize = 45
    val titleGap = 20

    val buffer: BufferedImage

    val drawPanel = JPanel()

    init {
        size = Dimension(512 + circleSize * 3, 372 + circleSize * 3 + titleGap)
        buffer = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB)

        layout = BorderLayout()

        add(drawPanel)
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

                val graphics = buffer.graphics as Graphics2D
                graphics.color = Color.WHITE
                graphics.fillRect(0, 0, buffer.width, buffer.height)

                graphics.color = Color.BLACK
                toRender.forEach { hitObject ->
                    fun drawCircle(pos: Point, size: Int) {
                        //graphics.fillOval()
                        graphics.drawArc(
                            (pos.xInt + circleSize + (circleSize - size) / 2),
                            (pos.yInt + circleSize + (circleSize - size) / 2),
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
                                (currentPoint.xInt + circleSize * 1.5).toInt(),
                                (currentPoint.yInt + circleSize * 1.5).toInt(),
                                (nextPoint.xInt + circleSize * 1.5).toInt(),
                                (nextPoint.yInt + circleSize * 1.5).toInt()
                            )
                        }
                    }
                    if(hitObject is Spinner) {
                        graphics.drawString("spinner is arriving", size.width / 2, size.height / 2)
                    }
                    //repaint()
                }
                drawPanel.graphics.drawImage(buffer, 0, 0, null)

                Thread.sleep(10L)
            }
        }
    }




}