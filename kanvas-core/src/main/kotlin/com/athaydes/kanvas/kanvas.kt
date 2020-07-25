package com.athaydes.kanvas

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import javafx.stage.Stage

abstract class KanvasApp : Application() {

    abstract fun draw(): Kanvas

    override fun start(primaryStage: Stage) {
        val kanvas = draw()
        val root = Group(kanvas.node)
        primaryStage.isResizable = false
        primaryStage.scene = Scene(root)
        root.layoutBoundsProperty().addListener { _ ->
            primaryStage.width = root.layoutBounds.width
            primaryStage.height = root.layoutBounds.height
        }
        primaryStage.centerOnScreen()
        primaryStage.show()
    }
}

class Kanvas(width: Double, height: Double) {
    private val canvas = Canvas(width, height)
    private val pane = BorderPane(canvas)

    private val ctx = canvas.graphicsContext2D

    var x: Double = 0.0
    var y: Double = 0.0

    private var fontColor: Paint = Color.BLACK

    val node: Node get() = pane

    fun clear() {
        ctx.clearRect(0.0, 0.0, canvas.width, canvas.height)
    }

    fun at(x: Double, y: Double): Kanvas {
        this.x = x
        this.y = y
        return this
    }

    @JvmOverloads
    fun font(font: Font, color: Paint? = null): Kanvas {
        ctx.font = font
        if (color != null) fontColor = color
        return this
    }

    fun fontColor(paint: Paint): Kanvas {
        fontColor = paint
        return this
    }

    @JvmOverloads
    fun background(paint: Paint, radii: CornerRadii? = null, insets: Insets? = null): Kanvas {
        pane.background = Background(BackgroundFill(paint, radii, insets))
        return this
    }

    @JvmOverloads
    fun stroke(paint: Paint? = null, width: Double? = null): Kanvas {
        if (paint != null) ctx.stroke = paint
        if (width != null) ctx.lineWidth = width
        return this
    }

    fun fill(paint: Paint): Kanvas {
        ctx.fill = paint
        return this
    }

    fun lineTo(x: Double, y: Double): Kanvas {
        ctx.moveTo(this.x, this.y)
        ctx.strokeLine(this.x, this.y, x, y)
        return this
    }

    @JvmOverloads
    fun rectangle(width: Double = 10.0, height: Double = 10.0, fill: Boolean = false): Kanvas {
        if (fill) {
            ctx.fillRect(x, y, width, height)
        } else {
            ctx.strokeRect(x, y, width, height)
        }
        return this
    }

    @JvmOverloads
    fun square(side: Double = 10.0, fill: Boolean = false): Kanvas {
        return rectangle(side, side, fill)
    }

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

    @JvmOverloads
    fun oval(width: Double = 10.0, height: Double = 10.0, fill: Boolean = false): Kanvas {
        if (fill) {
            ctx.fillOval(x, y, width, height)
        } else {
            ctx.strokeOval(x, y, width, height)
        }
        return this
    }

    @JvmOverloads
    fun arc(
        width: Double = 10.0, height: Double = 10.0, fill: Boolean = false,
        startAngle: Double = 10.0, arcExtent: Double = 10.0, closure: ArcType = ArcType.OPEN
    ): Kanvas {
        if (fill) {
            ctx.fillArc(x, y, width, height, startAngle, arcExtent, closure)
        } else {
            ctx.strokeArc(x, y, width, height, startAngle, arcExtent, closure)
        }
        return this
    }

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

    fun text(text: String): Kanvas {
        val currentFill = ctx.fill
        ctx.fill = fontColor
        ctx.fillText(text, x, y)
        ctx.fill = currentFill
        return this
    }

    fun point(x: Double, y: Double) = Point2D(x, y)

}
