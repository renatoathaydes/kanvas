import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.gr.KanvasObject
import com.athaydes.kanvas.gr.KanvasScript
import groovy.beans.Bindable
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import javafx.geometry.BoundingBox
import javafx.scene.paint.Color
import javafx.scene.text.Font

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Demo'
width 340
height 130

background Color.WHITE

// shared state
isActivated = new ActivatedState(on: false)

// UI components
button = new Button(x: 20, y: 40, w: 150, h: 50, activatedState: isActivated, text: 'Click Me!')
circle = new Circle(x: 200, y: 20, radius: 50, activatedState: isActivated)

kanvasObjects(button, circle)

@CompileStatic
@Bindable
class ActivatedState {
    boolean on
}

@CompileStatic
@Bindable
class Circle implements KanvasObject {
    double x, y, radius
    ActivatedState activatedState

    @Override
    void draw(Kanvas kanvas) {
        kanvas.fill activatedState.on ? Color.RED : Color.WHITE
        kanvas.at x, y circle(radius, true) stroke Color.BLACK circle(radius)
    }

}

@CompileStatic
@Bindable
class Button implements KanvasObject {
    double x, y, w, h
    boolean isMouseOn
    String text
    long justClickedTime
    ActivatedState activatedState
    BoundingBox bounds

    @Override
    void init(Kanvas kanvas) {
        bounds = kanvas.bounds(x, y, w, h)
        kanvas.mouse.onClick {
            if (isMouseOn) {
                activatedState.on = !activatedState.on
                setJustClickedTime 120
            }
        }
        kanvas.mouse.onMove {
            def isOn = kanvas.mouse.isOn(bounds)
            if (isMouseOn != isOn) {
                setIsMouseOn isOn
            }
        }
    }

    @Override
    void update(Kanvas kanvas, long dt) {
        if (justClickedTime > 0) {
            setJustClickedTime justClickedTime - dt
        }
    }

    @Override
    void draw(Kanvas kanvas) {
        kanvas.fill(justClickedTime > 0 ? Color.PURPLE :
                isMouseOn ? Color.INDIGO : Color.LIGHTGRAY)
        kanvas.at x, y rectangle(w, h, true)
        kanvas.font(Font.font(24), isMouseOn ? Color.WHITE : Color.BLACK)
        kanvas.at x + 28, y + 32 text(text)
    }

}
