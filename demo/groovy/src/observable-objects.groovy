import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.Mouse
import com.athaydes.kanvas.ObservableKanvasObject
import com.athaydes.kanvas.gr.KanvasScript
import groovy.beans.Bindable
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Observable Objects Demo'
width 300
height 250

// create the observable kanvas objects
def circle1 = new DraggableCircle()
def circle2 = new DraggableCircle(x: 150)

// tell Kanvas to manage the observable objects
manageKanvasObjects circle1, circle2

@CompileStatic
@Bindable
class DraggableCircle extends ObservableKanvasObject {
    double x = 50, y = 100
    final double radius = 25
    Color fill = Color.BLUE

    Shape getShape() { new Circle(x + radius, y + radius, radius) }

    @Override
    void init(Kanvas kanvas) {
        def mouse = kanvas.mouse
        mouse.onMove { evt ->
            setFill(isOn(mouse) ? Color.CYAN : Color.BLUE)
        }
        mouse.onDrag({ isOn(mouse).tap { dragging = it } }) { MouseEvent evt ->
            if (dragging) setCenter(evt.x, evt.y)
        }
    }

    @Override
    void draw(Kanvas kanvas) {
        kanvas.at(x, y) fill(fill) circle(radius, true)
    }

    private isOn(Mouse mouse) {
        shape.contains(mouse.location)
    }

    private void setCenter(double x, double y) {
        setX x - radius
        setY y - radius
    }
}
