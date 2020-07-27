package com.athaydes.kanvas

import javafx.embed.swing.SwingFXUtils
import javafx.scene.SnapshotParameters
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Save this [Kanvas] to an image.
 *
 * The [formatName] parameter is passed to [ImageIO.write].
 */
fun Kanvas.saveToImage(file: File, formatName: String = "png") {
    val writableImage = WritableImage(width.toInt(), height.toInt())
    node.snapshot(SnapshotParameters().apply { fill = Color.TRANSPARENT }, writableImage)
    val renderedImage: RenderedImage = SwingFXUtils.fromFXImage(writableImage, null)
    ImageIO.write(renderedImage, formatName, file)
}
