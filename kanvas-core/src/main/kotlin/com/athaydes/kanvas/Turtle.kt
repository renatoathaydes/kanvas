package com.athaydes.kanvas

import javafx.scene.paint.Paint

/**
 * A [Logo Turtle](https://en.wikipedia.org/wiki/Logo_(programming_language)).
 */
class Turtle(
    private val kanvas: Kanvas,
    private val ctx: KanvasContext
) {

    private var on: Boolean = true
    private val initialX: Double = kanvas.x
    private val initialY: Double = kanvas.y
    private var angle: Double = 0.0

    init {
        kanvas.at(0.0, 0.0)

        // turtle's perspective: always at zero coordinates which start from the Kanvas' current position.
        ctx.translate(initialX, initialY)
    }

    internal fun done() {
        kanvas.x = initialX
        kanvas.y = initialY
    }

    /**
     * Turn on the pencil. This is on by default, but can be turned off with [off].
     */
    fun on(): Turtle {
        this.on = true
        return this
    }

    /**
     * Turn off the pencil. To turn it back on, use [on].
     */
    fun off(): Turtle {
        this.on = false
        return this
    }

    /**
     * Move a certain distance on whatever direction the turtle is currently pointing at.
     *
     * Use [rotate], [left], [right], [up] or [down] to change the turtle's direction.
     *
     * @param distance the distance to move ahead
     */
    fun move(distance: Double): Turtle {
        if (on) kanvas.lineTo(0.0, distance)
        ctx.translate(0.0, distance)
        return this
    }

    /**
     * Rotate by a certain angle, in degrees.
     *
     * @param angle to rotate by, in degrees
     */
    fun rotate(angle: Double): Turtle {
        ctx.rotate(angle)
        this.angle += angle
        return this
    }

    /**
     * Change the scale.
     *
     * @param scale the factor by which all distances are multiplied
     */
    fun scale(scale: Double): Turtle {
        ctx.scale(scale, scale)
        return this
    }

    /**
     * Turn left relative to the current direction.
     *
     * This is equivalent to `rotate(-90.0)`.
     */
    fun left(): Turtle {
        return rotate(-90.0)
    }

    /**
     * Turn right relative to the current direction.
     *
     * This is equivalent to `rotate(90.0)`.
     */
    fun right(): Turtle {
        return rotate(90.0)
    }

    /**
     * Set the pencil paint (or color).
     *
     * @param paint the pencil paint or color
     */
    fun color(paint: Paint): Turtle {
        kanvas.stroke(paint)
        return this
    }

    /**
     * Turn to the UP direction.
     *
     * This is an "absolute" direction, i.e. no matter which direction the turtle is currently
     * pointing at, it will point UP after calling this method.
     */
    fun up(): Turtle {
        return down().rotate(180.0)
    }

    /**
     * Turn to the DOWN direction.
     *
     * This is an "absolute" direction, i.e. no matter which direction the turtle is currently
     * pointing at, it will point DOWN after calling this method.
     */
    fun down(): Turtle {
        val a = angle % 360.0
        rotate(-a)
        angle = 0.0
        return this
    }

}
