package com.athaydes.kanvas

import javafx.event.EventHandler
import javafx.geometry.BoundingBox
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Key used to de-register a handler.
 * @see Mouse.forgetClickHandler
 * @see Mouse.onClick
 */
data class HandlerKey(val key: String)

/**
 * Mouse state.
 */
class Mouse internal constructor(pane: Node) {

    private val x: AtomicLong = AtomicLong(0)
    private val y: AtomicLong = AtomicLong(0)

    private val clickHandlerCounter = AtomicInteger()
    private val clickHandlers = mutableMapOf<HandlerKey, (MouseEvent) -> Unit>()

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
        scene.onMouseMoved = EventHandler { evt ->
            x.set(evt.sceneX.toLong())
            y.set(evt.sceneY.toLong())
        }
        scene.onMouseClicked = EventHandler { evt ->
            clickHandlers.values.forEach { it(evt) }
        }
    }

    /**
     * Register a handler for mouse click events.
     *
     * The returned [HandlerKey] can be used to de-register the handler by calling
     * [forgetClickHandler].
     */
    fun onClick(handler: (MouseEvent) -> Unit): HandlerKey {
        val key = HandlerKey(clickHandlerCounter.incrementAndGet().toString())
        clickHandlers[key] = handler
        return key
    }

    /**
     * Forget a click handler previously registered with [onClick].
     */
    fun forgetClickHandler(key: HandlerKey): Boolean {
        return clickHandlers.remove(key) != null
    }

    /**
     * Check whether the mouse is currently within the given `box`.
     */
    fun isOn(box: BoundingBox): Boolean {
        val x = x.get()
        val y = y.get()
        return box.minX <= x && x <= box.maxX && box.minY <= y && y <= box.maxY
    }

}
