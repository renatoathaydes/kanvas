import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import javafx.application.Application
import javafx.scene.paint.Color

object KanvasDemo {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size == 1 && args[0] == "animation") {
            Application.launch(AnimationKanvasDemo::class.java)
        } else {
            Application.launch(BasicDemo::class.java)
        }
    }
}

class BasicDemo : KanvasApp() {
    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            title("Kotlin Kanvas Demo")

            // draw your shapes here!
            background(Color.BLACK).fill(Color.BLUE).stroke(paint = Color.GREEN, width = 3.0)
            at(50.0, 30.0).circle(radius = 100.0, fill = true)
        }
    }
}
