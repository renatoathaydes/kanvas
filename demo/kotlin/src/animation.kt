import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import javafx.scene.paint.Color

class AnimationKanvasDemo : KanvasApp() {

    data class KanvasCircle(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var vx: Double = 0.3,
        var vy: Double = 0.2,
        val diameter: Double = 40.0,
        var color: Color = Color.BLACK
    ) {
        fun update(k: Kanvas, dt: Long) {
            x += vx * dt
            y += vy * dt
            if (x + diameter > k.width || x < 0) {
                vx *= -1
                x = if (x < 0) 0.0 else k.width - diameter
            }
            if (y + diameter > k.height || y < 0) {
                vy *= -1
                y = if (y < 0) 0.0 else k.height - diameter
            }
            k.fill(color).at(x, y).circle(radius = diameter / 2, fill = true)
        }
    }

    val circles = listOf(
        KanvasCircle(color = Color.BLUE),
        KanvasCircle(color = Color.GREEN, x = 100.0, y = 140.0)
    )

    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            title("Kanvas Animation Demo")
            background(Color.INDIGO)
            loop { dt ->
                clear()
                circles.forEach { s -> s.update(this, dt) }
            }
        }
    }
}
