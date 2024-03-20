package com.athaydes.kanvas

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.stage.Screen
import javafx.stage.Stage
import kotlin.math.max

/**
 * Helpful base class for JavaFX Applications that display a [javafx.scene.canvas.Canvas]
 * object as the sole [javafx.scene.Node] in a [Scene].
 *
 * The [draw] method must be implemented to provide the contents for the [Kanvas].
 *
 * Optionally, the [configure] method may be implemented to configure the [Stage] after the
 * Kanvas has been created.
 *
 * Resizing the [Kanvas] object causes the [Scene] to resize to accommodate it, and vice-versa
 * (the [Kanvas] is not resizable by default, but can be made so by calling [resizable]).
 */
abstract class KanvasApp : Application() {

    /**
     * Draw the contents of the [Kanvas].
     */
    abstract fun draw(): Kanvas

    open fun configure(kanvas: Kanvas, primaryStage: Stage) {
    }

    override fun start(primaryStage: Stage) {
        val kanvas = draw()
        configure(kanvas, primaryStage)
        primaryStage.isResizable = kanvas.resizable
        val root = if (primaryStage.isResizable) {
            val (maxWidth, maxHeight) = getMaxScreenSize()
            primaryStage.maxWidth = maxWidth
            primaryStage.maxHeight = maxHeight
            createScrollPane(kanvas)
        } else {
            Group(kanvas.node)
        }
        primaryStage.scene = Scene(root)
        primaryStage.titleProperty().bindBidirectional(kanvas.titleProperty)
        primaryStage.centerOnScreen()
        primaryStage.show()
    }

    private fun createScrollPane(kanvas: Kanvas): Parent {
        val pane = ScrollPane(kanvas.node)
        pane.style = "-fx-focus-color: transparent;";
        return pane
    }

    private fun getMaxScreenSize(): Pair<Double, Double> {
        var width = Screen.getPrimary().bounds.width
        var height = Screen.getPrimary().bounds.height
        Screen.getScreens().forEach { s ->
            width = max(width, s.bounds.width)
            height = max(height, s.bounds.height)
        }
        return width to height
    }
}
