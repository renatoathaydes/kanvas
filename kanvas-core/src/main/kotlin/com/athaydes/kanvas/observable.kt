package com.athaydes.kanvas

import java.lang.System.Logger.Level.DEBUG
import java.lang.System.Logger.Level.TRACE
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Base class for Kanvas objects which contain observable properties.
 */
abstract class ObservableKanvasObject {

    private companion object {
        private val log = System.getLogger(ObservableKanvasObject::class.qualifiedName)

        private var currentDraggable: ObservableKanvasObject? = null
    }

    val isChanged: AtomicBoolean = AtomicBoolean(true)

    private val properties: MutableList<ReadWriteProperty<Any?, out Any?>> = mutableListOf()

    /**
     * Whether this object is being dragged by the mouse.
     *
     * Kanvas ensures that only one object can be dragged at any time.
     * If any object tries to set this value to `true` while another object is being dragged,
     * Kanvas will immediately set this back to `false`.
     */
    var isDragging: Boolean by Delegates.vetoable(false) { _, old, newValue ->
        if (newValue == old) {
            true
        } else if (newValue) {
            if (currentDraggable == null) {
                log.log(DEBUG, "Setting current draggable element to {0}", this)
                currentDraggable = this
                true
            } else {
                log.log(TRACE, "Ignoring draggable element update, already dragging something else")
                false
            }
        } else {
            if (currentDraggable === this) {
                log.log(TRACE, "Unsetting draggable element")
                currentDraggable = null
            }
            true
        }
    }

    /**
     * Implement this method to be notified of modifications to any observable property.
     *
     * @param property being changed
     * @param oldValue the old value
     * @param newValue the new value
     */
    open fun onChange(property: KProperty<Any?>, oldValue: Any?, newValue: Any?) {
    }

    /**
     * Create a [ReadWriteProperty] that will be observable.
     *
     * Example usage:
     *
     * ```kotlin
     * class Example: ObservableKanvasObject() {
     *   var x: Int by observable(0)
     * }
     * ```
     *
     * When the value of an observable property changes, the Kanvas object is automatically redrawn.
     */
    fun <T> observable(initialValue: T): ReadWriteProperty<Any?, T> {
        val prop = Delegates.observable(initialValue) { p, old, new ->
            val hasChanged = old != new
            if (!isChanged.compareAndSet(hasChanged, hasChanged)) {
                log.log(
                    TRACE, "Property \"{0}\" of {1} has changed from \"{2}\" to \"{3}\"",
                    p.name, this, old, new
                )
                onChange(p, old, new)
            }
        }
        properties.add(prop)
        return prop
    }

    /**
     * Initialize this object.
     *
     * This method is only called once, before the Kanvas is first drawn.
     */
    open fun init(kanvas: Kanvas) {
    }

    /**
     * Draw this object on the Kanvas.
     */
    abstract fun draw(kanvas: Kanvas)
}
