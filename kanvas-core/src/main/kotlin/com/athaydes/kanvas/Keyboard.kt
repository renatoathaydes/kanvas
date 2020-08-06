package com.athaydes.kanvas

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.KeyCode

/**
 * Keyboard state.
 */
class Keyboard internal constructor(pane: Node) {
    private val pressedKeys = mutableSetOf<KeyCode>()

    init {
        if (pane.scene == null) {
            pane.sceneProperty().addListener { _ ->
                addEventHandlers(pane.scene)
            }
        } else {
            addEventHandlers(pane.scene)
        }
    }

    private fun addEventHandlers(scene: Scene) {
        scene.onKeyPressed = EventHandler { evt ->
            pressedKeys += evt.code
        }
        scene.onKeyReleased = EventHandler { evt ->
            pressedKeys -= evt.code
        }
    }

    /**
     * Returns true if the given key is pressed, false otherwise.
     */
    fun isDown(key: KeyCode): Boolean {
        return pressedKeys.contains(key)
    }

}