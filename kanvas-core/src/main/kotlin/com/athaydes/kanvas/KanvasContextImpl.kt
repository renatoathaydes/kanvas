package  com.athaydes.kanvas

import javafx.scene.canvas.GraphicsContext
import javafx.scene.effect.Effect
import javafx.scene.transform.Affine

internal class KanvasContextImpl(private val g: GraphicsContext) : KanvasContext {
    override fun translate(x: Double, y: Double) = g.translate(x, y)

    override fun scale(x: Double, y: Double) = g.scale(x, y)

    override fun rotate(degrees: Double) = g.rotate(degrees)

    override fun transform(mxx: Double, myx: Double, mxy: Double, myy: Double, mxt: Double, myt: Double) =
        g.transform(mxx, myx, mxy, myy, mxt, myt)

    override fun transform(xform: Affine?) = g.transform(xform)

    override fun setTransform(mxx: Double, myx: Double, mxy: Double, myy: Double, mxt: Double, myt: Double) =
        g.setTransform(mxx, myx, mxy, myy, mxt, myt)

    override fun setEffect(e: Effect?) = g.setEffect(e)

    override fun applyEffect(e: Effect?) = g.applyEffect(e)

}