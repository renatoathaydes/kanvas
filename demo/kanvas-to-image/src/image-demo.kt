import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import com.athaydes.kanvas.saveToImage
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.paint.Color
import java.io.File

object ImageDemoKt {
    @JvmStatic
    fun main(vararg args: String) {
        Application.launch(KanvasToImageDemo::class.java, *args)
    }
}

class KanvasToImageDemo : KanvasApp() {

    lateinit var file: File

    override fun init() {
        if (parameters.raw.size != 1) {
            throw Exception("Please provide a single argument with the name of the file to save the image to.")
        }
        file = File(parameters.raw[0])
    }

    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            // draw your shapes here!
            background(Color.BLACK).fill(Color.BLUE).stroke(paint = Color.YELLOW, width = 2.0)
            at(50.0, 30.0)
            circle(radius = 100.0, fill = true)
            square(side = 200.0)

            Platform.runLater {
                saveToImage(file, file.extension.ifBlank { "png" })
            }
        }
    }
}
