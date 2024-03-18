package com.athaydes.kanvas

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

/**
 * Keyboard state and container of event handlers.
 */
class Keyboard internal constructor(pane: Node) {
    private val pressedKeys = mutableSetOf<KeyCode>()
    private val handlerCounter = AtomicInteger()
    private val keyPressedHandlers = mutableMapOf<HandlerKey, Consumer<KeyEvent>>()
    private val keyReleasedHandlers = mutableMapOf<HandlerKey, Consumer<KeyEvent>>()

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
            keyPressedHandlers.values.forEach { it.accept(evt) }
        }
        scene.onKeyReleased = EventHandler { evt ->
            pressedKeys -= evt.code
            keyReleasedHandlers.values.forEach { it.accept(evt) }
        }
    }

    /**
     * Remove all event handlers.
     */
    fun clear() {
        Platform.runLater {
            keyPressedHandlers.clear()
            keyReleasedHandlers.clear()
            handlerCounter.set(0)
        }

    }

    /**
     * Returns true if the given key is pressed, false otherwise.
     */
    fun isDown(key: KeyCode): Boolean {
        return pressedKeys.contains(key)
    }

    /**
     * Register a handler for key press events.
     *
     * The returned [HandlerKey] can be used to de-register the handler by calling
     * [forgetHandler].
     */
    fun onKeyPressed(handler: Consumer<KeyEvent>): HandlerKey {
        val key = HandlerKey(handlerCounter.incrementAndGet().toString())
        keyPressedHandlers[key] = handler
        return key
    }

    /**
     * Register a handler for key release events.
     *
     * The returned [HandlerKey] can be used to de-register the handler by calling
     * [forgetHandler].
     */
    fun onKeyReleased(handler: Consumer<KeyEvent>): HandlerKey {
        val key = HandlerKey(handlerCounter.incrementAndGet().toString())
        keyReleasedHandlers[key] = handler
        return key
    }

    /**
     * Forget an event handler previously registered with [onKeyPressed] or [onKeyReleased].
     */
    fun forgetHandler(key: HandlerKey): Boolean {
        return keyReleasedHandlers.remove(key) != null
                || keyPressedHandlers.remove(key) != null
    }
}
