package com.athaydes.kanvas

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Base class for Kanvas objects which contain observable properties.
 */
abstract class ObservableKanvasObject {

    private companion object {
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
                currentDraggable = this
                true
            } else false
        } else {
            if (currentDraggable === this) currentDraggable = null
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
            if (!isChanged.get()) isChanged.set(old != new)
            if (isChanged.get()) onChange(p, old, new)
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
