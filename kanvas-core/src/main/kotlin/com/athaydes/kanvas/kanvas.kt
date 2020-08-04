package com.athaydes.kanvas

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.BoundingBox
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import javafx.stage.Stage
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * A nice interface for JavaFX's [Canvas] which makes it very easy to draw shapes and text on the screen.
 *
 * Example usage:
 *
 * ```
 * val kanvas = Kanvas(400.0, 300.0).apply {
 *     stroke(Color.RED, width = 3.0)
 *     at(20, 20).circle (50)
 * }
 * parent.getChildren().add(kanvas.node)
 * ```
 */
class Kanvas(width: Double, height: Double) {
    private val canvas = Canvas(width, height)
    private val pane = BorderPane(canvas)

    private var loopPeriod = Duration.ofMillis(16)
    private var loopFuture: ScheduledFuture<*>? = null
    private val executor: ScheduledExecutorService by lazy {
        Executors.newSingleThreadScheduledExecutor {
            Thread(it).apply {
                name = "kanvas-looper"
                isDaemon = true
            }
        }
    }

    private val ctx = canvas.graphicsContext2D

    var x: Double = 0.0
    var y: Double = 0.0

    private var fontColor: Paint = Color.BLACK

    val titleProperty = SimpleStringProperty()

    val node: Node get() = pane
    val width: Double get() = canvas.width
    val height: Double get() = canvas.height

    /**
     * Set the title of this kanvas.
     *
     * This has no effect unless [titleProperty] is bound to a JavaFX component, such at the [Stage]
     * (which is done by [KanvasApp], for example).
     */
    fun title(text: String) {
        titleProperty.set(text)
    }

    /**
     * Set the period of the looper function, in milliseconds (in other words, how often the looper function should run).
     *
     * The period must be a positive number, or zero to stop the loop.
     *
     * This method must be called before the [loop] method is called to have any effect.
     */
    fun loopPeriod(millis: Long) {
        loopPeriod(Duration.ofMillis(millis))
    }

    /**
     * Set the period of the looper function (in other words, how often the looper function should run).
     *
     * The duration must be positive, or zero to stop the loop.
     *
     * This method must be called before the [loop] method is called to have any effect.
     */
    fun loopPeriod(duration: Duration) {
        if (duration.isNegative) throw IllegalArgumentException("loop period must be a positive duration")
        loopPeriod = duration
    }

    /**
     * Set an updater function to run in a loop and update the Kanvas.
     *
     * A looper is normally used to create Kanvas animations. The [dt] argument passed to the [update]
     * function is the delta time, in milliseconds, between invocations. It can be used to easily produce
     * animations that run at the same speed regardless of the frame rate.
     *
     * Use the [loopPeriod] to change the frame rate, i.e. how often the given [update] function should run.
     */
    fun loop(update: (dt: Long) -> Unit) {
        loopFuture?.cancel(false)
        if (loopPeriod == Duration.ZERO) return // stop the loop
        val period = loopPeriod.toMillis()
        val dt = AtomicLong(System.currentTimeMillis())
        loopFuture = executor.scheduleAtFixedRate({
            Platform.runLater {
                val t = System.currentTimeMillis()
                val dtVal = t - dt.getAndSet(t)
                try {
                    update(dtVal)
                } catch (e: Exception) {
                    System.err.println(e)
                }
            }
        }, period, period, TimeUnit.MILLISECONDS)
    }

    /**
     * Clear the whole [Canvas].
     */
    fun clear() {
        ctx.clearRect(0.0, 0.0, canvas.width, canvas.height)
    }

    /**
     * Set the coordinates where drawing should occur.
     *
     * Coordinates start from the top-left corner, so for example, (0, 0) is the top-left corner,
     * (10, 20) is 10 pixels from the left, 20 pixels from the top.
     *
     * When drawing a shape, the shape's top-left corner will be the location specified by calling this method.
     *
     * So, for example, the following square will have coordinates `(10, 20), (20, 20), (20, 30), (10, 30)`:
     *
     * ```
     * at (10, 20).rectangle (10, 10)
     * ```
     */
    fun at(x: Double, y: Double): Kanvas {
        this.x = x
        this.y = y
        return this
    }

    /**
     * Set the font to be used when using [text].
     */
    @JvmOverloads
    fun font(font: Font, color: Paint? = null): Kanvas {
        ctx.font = font
        if (color != null) fontColor = color
        return this
    }

    /**
     * Set the color of the text to be used with [text].
     */
    fun fontColor(paint: Paint): Kanvas {
        fontColor = paint
        return this
    }

    /**
     * Set the color and other properties of the [Canvas]'s background.
     */
    @JvmOverloads
    fun background(paint: Paint, radii: CornerRadii? = null, insets: Insets? = null): Kanvas {
        pane.background = Background(BackgroundFill(paint, radii, insets))
        return this
    }

    /**
     * Set the stroke (used when drawing shapes without filling).
     * @see fill
     */
    @JvmOverloads
    fun stroke(paint: Paint? = null, width: Double? = null): Kanvas {
        if (paint != null) ctx.stroke = paint
        if (width != null) ctx.lineWidth = width
        return this
    }

    /**
     * Set the fill (used when drawing shapes with filling).
     * @see stroke
     */
    fun fill(paint: Paint): Kanvas {
        ctx.fill = paint
        return this
    }

    /**
     * Draw a line from the current location (set by [at]) to the given coordinates.
     */
    fun lineTo(x: Double, y: Double): Kanvas {
        ctx.moveTo(this.x, this.y)
        ctx.strokeLine(this.x, this.y, x, y)
        return this
    }

    /**
     * Draw a rectangle at the current location (set by [at]).
     */
    @JvmOverloads
    fun rectangle(width: Double = 10.0, height: Double = 10.0, fill: Boolean = false): Kanvas {
        if (fill) {
            ctx.fillRect(x, y, width, height)
        } else {
            ctx.strokeRect(x, y, width, height)
        }
        return this
    }

    /**
     * Draw a square at the current location (set by [at]).
     */
    @JvmOverloads
    fun square(side: Double = 10.0, fill: Boolean = false): Kanvas {
        return rectangle(side, side, fill)
    }

    /**
     * Draw a circle at the current location (set by [at]).
     */
    @JvmOverloads
    fun circle(radius: Double = 10.0, fill: Boolean = false): Kanvas {
        val diameter = radius * 2.0
        if (fill) {
            ctx.fillOval(x, y, diameter, diameter)
        } else {
            ctx.strokeOval(x, y, diameter, diameter)
        }
        return this
    }

    /**
     * Draw an oval at the current location (set by [at]).
     */
    @JvmOverloads
    fun oval(width: Double = 10.0, height: Double = 10.0, fill: Boolean = false): Kanvas {
        if (fill) {
            ctx.fillOval(x, y, width, height)
        } else {
            ctx.strokeOval(x, y, width, height)
        }
        return this
    }

    /**
     * Draw an arc at the current location (set by [at]).
     *
     * An arc is an interval of an oval with a certain [width] and [height], where the [startAngle] determines
     * where the arc starts (in degrees, relative to the point in the arc with the largest x value), and [arcExtent]
     * determines the extent of the arc in degrees, increasing anti-clockwise.
     *
     * So, for example, an arc starting at `startAngle = 0` and `arcExtent = 180` will start at the right-most point
     * of the arc, and extend all the way to the left-most point of the arc, as if moving anti-clockwise.
     */
    @JvmOverloads
    fun arc(
        width: Double = 10.0, height: Double = 10.0, fill: Boolean = false,
        startAngle: Double = 0.0, arcExtent: Double = 90.0, closure: ArcType = ArcType.OPEN
    ): Kanvas {
        if (fill) {
            ctx.fillArc(x, y, width, height, startAngle, arcExtent, closure)
        } else {
            ctx.strokeArc(x, y, width, height, startAngle, arcExtent, closure)
        }
        return this
    }

    /**
     * Draw a polygon at the current location (set by [at]).
     *
     * All [points] are relative to the [at] position.
     *
     * The example below will create a square with coordinates `(10, 20), (20, 20), (20, 30), (10, 30)`.
     *
     * ```
     * at (10, 20).polygon (point(0, 0), point(10, 0), point(10, 10), point(0, 10))
     * ```
     *
     * @see point
     */
    @JvmOverloads
    fun polygon(points: List<Point2D>, fill: Boolean = false): Kanvas {
        val xs = points.map { x + it.x }.toDoubleArray()
        val ys = points.map { y + it.y }.toDoubleArray()
        if (fill) {
            ctx.fillPolygon(xs, ys, xs.size)
        } else {
            ctx.strokePolygon(xs, ys, xs.size)
        }
        return this
    }

    /**
     * Draw the given text at the current location (set by [at]).
     *
     * @see font
     * @see fontColor
     */
    fun text(text: String): Kanvas {
        val currentFill = ctx.fill
        ctx.fill = fontColor
        ctx.fillText(text, x, y)
        ctx.fill = currentFill
        return this
    }

    /**
     * Draw an [Image].
     *
     * The [width] and [height] are the size of the image to be drawn, while the [bounds] are relative to the
     * image itself.
     */
    @JvmOverloads
    fun image(image: Image, width: Double = 10.0, height: Double = 10.0, bounds: BoundingBox? = null): Kanvas {
        val b = bounds ?: bounds(0.0, 0.0, width, height)
        ctx.drawImage(image, b.minX, b.minY, b.width, b.height, x, y, width, height)
        return this
    }

    /**
     * Create a [Point2D].
     */
    fun point(x: Double, y: Double) = Point2D(x, y)

    /**
     * Create a [BoundingBox].
     *
     * See [image].
     */
    @JvmOverloads
    fun bounds(x: Double = 0.0, y: Double = 0.0, width: Double = 10.0, height: Double = 10.0): BoundingBox =
        BoundingBox(x, y, width, height)

}
