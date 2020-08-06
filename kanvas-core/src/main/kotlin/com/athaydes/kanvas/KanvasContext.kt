package com.athaydes.kanvas

import javafx.scene.effect.Effect
import javafx.scene.transform.Affine

/**
 * A context object that can be used to apply advanced transformations on the canvas
 * objects being drawn.
 *
 * To access it, use the [com.athaydes.kanvas.Kanvas.withContext] method.
 */
interface KanvasContext {

    /**
     * Translates the current transform by x, y.
     * @param x value to translate along the x axis.
     * @param y value to translate along the y axis.
     */
    fun translate(x: Double, y: Double)

    /**
     * Scales the current transform by x, y.
     * @param x value to scale in the x axis.
     * @param y value to scale in the y axis.
     */
    fun scale(x: Double, y: Double)

    /**
     * Rotates the current transform in degrees.
     * @param degrees value in degrees to rotate the current transform.
     */
    fun rotate(degrees: Double)

    /**
     * Concatenates the input with the current transform.
     *
     * @param mxx - the X coordinate scaling element of the 3x4 matrix
     * @param myx - the Y coordinate shearing element of the 3x4 matrix
     * @param mxy - the X coordinate shearing element of the 3x4 matrix
     * @param myy - the Y coordinate scaling element of the 3x4 matrix
     * @param mxt - the X coordinate translation element of the 3x4 matrix
     * @param myt - the Y coordinate translation element of the 3x4 matrix
     */
    fun transform(
        mxx: Double, myx: Double,
        mxy: Double, myy: Double,
        mxt: Double, myt: Double
    )

    /**
     * Concatenates the input with the current transform. Only 2D transforms are
     * supported. The only values used are the X and Y scaling, translation, and
     * shearing components of a transform. A `null` value is treated as identity.
     *
     * @param xform The affine to be concatenated with the current transform or null.
     */
    fun transform(xform: Affine?)

    /**
     * Sets the current transform.
     * @param mxx - the X coordinate scaling element of the 3x4 matrix
     * @param myx - the Y coordinate shearing element of the 3x4 matrix
     * @param mxy - the X coordinate shearing element of the 3x4 matrix
     * @param myy - the Y coordinate scaling element of the 3x4 matrix
     * @param mxt - the X coordinate translation element of the 3x4 matrix
     * @param myt - the Y coordinate translation element of the 3x4 matrix
     */
    fun setTransform(
        mxx: Double, myx: Double,
        mxy: Double, myy: Double,
        mxt: Double, myt: Double
    )

    /**
     * Sets the effect to be applied after the next draw call, or null to
     * disable effects.
     * The current effect is a [common attribute](#comm-attr)
     * used for nearly all rendering operations as specified in the
     * [Rendering Attributes Table](#attr-ops-table).
     *
     * @param e the effect to use, or null to disable effects
     */
    fun setEffect(e: Effect?)

    /**
     * Applies the given effect to the entire bounds of the canvas and stores
     * the result back into the same canvas.
     * A `null` value will be ignored.
     * The effect will be applied without any other rendering attributes and
     * under an Identity coordinate transform.
     * Since the effect is applied to the entire bounds of the canvas, some
     * effects may have a confusing result, such as a Reflection effect
     * that will apply its reflection off of the bottom of the canvas even if
     * only a portion of the canvas has been rendered to and will not be
     * visible unless a negative offset is used to bring the reflection back
     * into view.
     *
     * @param e the effect to apply onto the entire destination or null.
     */
    fun applyEffect(e: Effect?)
}