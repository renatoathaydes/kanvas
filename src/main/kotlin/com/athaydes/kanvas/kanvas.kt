package com.athaydes.kanvas

import javafx.application.Application
import javafx.geometry.Insets
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
import javafx.stage.Stage

abstract class KanvasApp : Application() {

    abstract fun draw(): Kanvas

    override fun start(primaryStage: Stage) {
        val kanvas = draw()
        val root = Group(kanvas.node)
        primaryStage.scene = Scene(root)
        primaryStage.centerOnScreen()
        primaryStage.show()
    }
}

class Kanvas(val width: Double, val height: Double) {
    private val canvas = Canvas(width, height)
    private val pane = BorderPane(canvas)

    private val ctx = canvas.graphicsContext2D
    var x: Double = 0.0

    var y: Double = 0.0

    val node: Node get() = pane

    fun at(x: Double, y: Double): Kanvas {
        this.x = x
        this.y = y
        return this
    }

    fun background(paint: Paint, radii: CornerRadii? = null, insets: Insets? = null): Kanvas {
        pane.background = Background(BackgroundFill(paint, radii, insets))
        return this
    }

    fun stroke(paint: Paint? = null, width: Double? = null): Kanvas {
        if (paint != null) ctx.stroke = paint
        if (width != null) ctx.lineWidth = width
        return this
    }

    fun fill(paint: Paint): Kanvas {
        ctx.fill = paint
        return this
    }

    fun circle(radius: Double = 10.0, fill: Boolean = false) {
        val diameter = radius * 2.0
        if (fill) {
            ctx.fillOval(x, y, diameter, diameter)
        } else {
            ctx.strokeOval(x, y, diameter, diameter)
        }
    }

}

class KanvasDemo : KanvasApp() {
    override fun start(primaryStage: Stage) {
        primaryStage.title = "Kanvas Demo"
        super.start(primaryStage)
    }

    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            background(Color.YELLOW)
            stroke(Color.BLUE)
            fill(Color.BLUE)
            at(50.0, 50.0).circle(radius = 30.0, fill = true)
            stroke(paint = Color.GREEN, width = 3.0)
            at(110.0, 50.0).circle(radius = 30.0, fill = false)
        }
    }
}

fun main() {
    Application.launch(KanvasDemo::class.java)
}
