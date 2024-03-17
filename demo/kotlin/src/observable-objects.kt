import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import com.athaydes.kanvas.Mouse
import com.athaydes.kanvas.ObservableKanvasObject
import javafx.application.Application
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape

fun main(args: Array<String>) {
    Application.launch(ObservablesDemo::class.java, *args)
}

class ObservablesDemo : KanvasApp() {
    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            title("Kanvas Observable Objects Demo")

            // create the observable kanvas objects
            val (circle1, circle2) = DraggableCircle() to DraggableCircle()
            circle2.x += 100

            // tell Kanvas to manage the observable objects
            manageKanvasObjects(circle1, circle2)
        }
    }
}

class DraggableCircle : ObservableKanvasObject() {
    private val radius = 25.0
    var x by observable(50.0)
    var y by observable(100.0)
    var fill by observable(Color.BLUE)

    val shape: Shape get() = Circle(x + radius, y + radius, radius)

    override fun init(kanvas: Kanvas) {
        val mouse = kanvas.mouse
        mouse.onMove { fill = if (isOn(mouse)) Color.CYAN else Color.BLUE }
        // only one object is allowed to set isDragging to true at any given time,
        // which makes it simpler to avoid dragging anything that falls under the mouse.
        mouse.onDrag({ isOn(mouse).also { isDragging = it } }) { evt ->
            if (isDragging) setCenter(evt.x, evt.y)
        }
    }

    override fun draw(kanvas: Kanvas) {
        kanvas.at(x, y).fill(fill).circle(radius, fill = true)
    }

    private fun isOn(mouse: Mouse) = shape.contains(mouse.location)

    fun setCenter(x: Double, y: Double) {
        this.x = x - radius
        this.y = y - radius
    }
}