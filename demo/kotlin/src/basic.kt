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

class AnimationKanvasDemo : KanvasApp() {
    data class State(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var vx: Double = 5.0,
        var vy: Double = 3.0,
        var color: Color = Color.BLACK
    ) {
        fun update(k: Kanvas) {
            x += vx
            y += vy
            if (x > k.width - 40 || x < 0) vx *= -1
            if (y > k.height - 40 || y < 0) vy *= -1
            k.fill(color).at(x, y).circle(radius = 20.0, fill = true)
        }
    }

    val state = listOf(
        State(color = Color.BLUE),
        State(color = Color.GREEN, x = 100.0, y = 200.0)
    )

    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            title("Kanvas Animation Demo")
            background(Color.INDIGO)
            loop {
                clear()
                state.forEach { s -> s.update(this) }
            }
        }
    }
}
