import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import javafx.scene.paint.Color

class AnimationKanvasDemo : KanvasApp() {
    data class State(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var vx: Double = 0.3,
        var vy: Double = 0.2,
        var color: Color = Color.BLACK
    ) {
        fun update(k: Kanvas, dt: Long) {
            x += vx * dt
            y += vy * dt
            if (x > k.width - 40 || x < 0) vx *= -1
            if (y > k.height - 40 || y < 0) vy *= -1
            k.fill(color).at(x, y).circle(radius = 20.0, fill = true)
        }
    }

    val state = listOf(
        State(color = Color.BLUE),
        State(color = Color.GREEN, x = 100.0, y = 140.0)
    )

    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            title("Kanvas Animation Demo")
            background(Color.INDIGO)
            loop { dt ->
                clear()
                state.forEach { s -> s.update(this, dt) }
            }
        }
    }
}
