package com.athaydes.kanvas

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Helpful base class for JavaFX Applications that display a [Canvas] object as the sole [Node] in a [Scene].
 *
 * The [draw] method must be implemented to provide the contents for the [Canvas].
 *
 * Resizing the [Kanvas] object causes the [Scene] to resize to accommodate it.
 */
abstract class KanvasApp : Application() {

    /**
     * Draw the contents of the [Canvas].
     */
    abstract fun draw(): Kanvas

    override fun start(primaryStage: Stage) {
        val kanvas = draw()
        val root = Group(kanvas.node)
        primaryStage.titleProperty().bind(kanvas.titleProperty)
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