package com.athaydes.kanvas

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * Key used to de-register a handler.
 * @see Mouse.forgetHandler
 * @see Mouse.onClick
 */
data class HandlerKey internal constructor(val key: String)

private class Dragger(
    val accept: Predicate<MouseEvent>,
    val drag: Consumer<MouseEvent>,
)

/**
 * Mouse state and container of event handlers.
 */
class Mouse internal constructor(pane: Node) {

    private val x = AtomicLong(0)
    private val y = AtomicLong(0)

    val location get() = Point2D(x.get().toDouble(), y.get().toDouble())

    private val handlerCounter = AtomicInteger()
    private val clickHandlers = mutableMapOf<HandlerKey, Consumer<MouseEvent>>()
    private val dragHandlers = mutableMapOf<HandlerKey, Dragger>()
    private val moveHandlers = mutableMapOf<HandlerKey, Consumer<MouseEvent>>()

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
        fun mouseMoved(evt: MouseEvent) {
            x.set(evt.sceneX.toLong())
            y.set(evt.sceneY.toLong())
            moveHandlers.values.forEach { it.accept(evt) }
        }
        scene.onMouseMoved = EventHandler { evt -> mouseMoved(evt) }
        scene.onMouseClicked = EventHandler { evt ->
            clickHandlers.values.forEach { it.accept(evt) }
        }
        scene.onMouseDragged = EventHandler { evt ->
            mouseMoved(evt)
            Platform.runLater {
                dragHandlers.values.asSequence()
                    .filter { it.accept.test(evt) }
                    .forEach { it.drag.accept(evt) }
            }
        }
    }

    /**
     * Register a handler for mouse click events.
     *
     * The returned [HandlerKey] can be used to de-register the handler by calling
     * [forgetHandler].
     */
    fun onClick(handler: Consumer<MouseEvent>): HandlerKey {
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
    fun onMove(handler: Consumer<MouseEvent>): HandlerKey {
        val key = HandlerKey(handlerCounter.incrementAndGet().toString())
        moveHandlers[key] = handler
        return key
    }

    fun onDrag(
        acceptStart: Predicate<MouseEvent>,
        onDrag: Consumer<MouseEvent>,
    ): HandlerKey {
        val key = HandlerKey(handlerCounter.incrementAndGet().toString())
        dragHandlers[key] = Dragger(acceptStart, onDrag)
        return key
    }

    /**
     * Forget an event handler previously registered with [onClick], [onDrag] or [onMove].
     */
    fun forgetHandler(key: HandlerKey): Boolean {
        return clickHandlers.remove(key) != null
                || moveHandlers.remove(key) != null
                || dragHandlers.remove(key) != null
    }

    /**
     * Check whether the mouse is currently within the given `box`.
     */
    fun isOn(box: Bounds): Boolean {
        val x = x.get()
        val y = y.get()
        return box.minX <= x && x <= box.maxX && box.minY <= y && y <= box.maxY
    }

}
