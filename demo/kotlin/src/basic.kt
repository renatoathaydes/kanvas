import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import javafx.application.Application
import javafx.scene.paint.Color

fun main() {
    Application.launch(KanvasDemo::class.java)
}

class KanvasDemo : KanvasApp() {
    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            // draw your shapes here!
            background(Color.BLACK).fill(Color.BLUE).stroke(paint = Color.GREEN, width = 3.0)
            at(50.0, 30.0).circle(radius = 100.0, fill = true)
        }
    }
}
