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
 * @see Mouse.forgetHandler
 * @see Mouse.onClick
 */
data class HandlerKey(val key: String)

/**
 * Mouse state.
 */
class Mouse internal constructor(pane: Node) {

    private val x: AtomicLong = AtomicLong(0)
    private val y: AtomicLong = AtomicLong(0)

    private val handlerCounter = AtomicInteger()
    private val clickHandlers = mutableMapOf<HandlerKey, (MouseEvent) -> Unit>()
    private val moveHandlers = mutableMapOf<HandlerKey, (MouseEvent) -> Unit>()

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
            moveHandlers.values.forEach { it(evt) }
        }
        scene.onMouseClicked = EventHandler { evt ->
            clickHandlers.values.forEach { it(evt) }
        }
    }

    /**
     * Register a handler for mouse click events.
     *
     * The returned [HandlerKey] can be used to de-register the handler by calling
     * [forgetHandler].
     */
    fun onClick(handler: (MouseEvent) -> Unit): HandlerKey {
        val key = HandlerKey(handlerCounter.incrementAndGet().toString())
        clickHandlers[key] = handler
        return key
    }

    /**
     * Register a handler for mouse move events.
     *
     * The returned [HandlerKey] can be used to de-register the handler by calling
     * [forgetHandler].
     */
    fun onMove(handler: (MouseEvent) -> Unit): HandlerKey {
        val key = HandlerKey(handlerCounter.incrementAndGet().toString())
        moveHandlers[key] = handler
        return key
    }

    /**
     * Forget an event handler previously registered with [onClick] or [onMove].
     */
    fun forgetHandler(key: HandlerKey): Boolean {
        return clickHandlers.remove(key) != null || moveHandlers.remove(key) != null
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
