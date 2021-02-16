import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import javafx.geometry.BoundingBox
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Demo'
width 340
height 130

background Color.WHITE
fill Color.RED
stroke Color.BLACK

button = new Button(kanvas: kanvas, x: 20, y: 40, w: 150, h: 50, text: 'Click Me!')
mouse.onClick { button.onClick() }

loop { long dt ->
    clear()
    button.draw(dt)
    if (button.isOn) {
        fill Color.RED
        at 200, 20 circle(50, true)
    } else {
        at 200, 20 circle(50)
    }
}

@Canonical
@CompileStatic
class Button {
    double x, y, w, h
    Paint color = Color.LIGHTGRAY
    Paint hoverColor = Color.INDIGO
    Paint clickColor = Color.PURPLE
    Paint textColor = Color.BLACK
    Paint hoverTextColor = Color.WHITE
    String text = 'Button'
    Kanvas kanvas
    boolean isOn
    private long justClickedTime

    void onClick() {
        if (kanvas.mouse.isOn(boundingBox)) {
            isOn = !isOn
            justClickedTime = 120
        }
    }

    void draw(long dt) {
        def isHover = kanvas.mouse.isOn(boundingBox)
        if (justClickedTime > 0) {
            justClickedTime -= dt
        }
        kanvas.fill(justClickedTime > 0 ? clickColor : isHover ? hoverColor : color)
        kanvas.at x, y rectangle(w, h, true)
        kanvas.font(Font.font(24), isHover ? hoverTextColor : textColor)
        kanvas.at x + 28, y + 32 text(text)
    }

    BoundingBox getBoundingBox() {
        kanvas.bounds(x, y, w, h)
    }

}