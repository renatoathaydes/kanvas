import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.gr.KanvasObject
import com.athaydes.kanvas.gr.KanvasScript
import groovy.beans.Bindable
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import javafx.geometry.BoundingBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import kotlin.Unit

@BaseScript KanvasScript baseScript

title 'Counter'
width 175
height 150

background Color.ORCHID
font Font.font(36)

// UI components
def counter = new Counter()
def plusButton = new CounterButton('+', 1, 100, 20, counter)
def minusButton = new CounterButton('-', -1, 20, 20, counter)

kanvasObjects counter, plusButton, minusButton

@CompileStatic
@Bindable
class Counter implements KanvasObject {
    int value
    final double x = 75, y = 110
    final BoundingBox bounds = new BoundingBox(x, y - 36, 36, 36)

    Counter() {
        addPropertyChangeListener('value') {evt ->
            if (evt.newValue as int < 0) setValue(0)
        }
    }

    @Override
    void draw(Kanvas kanvas) {
        kanvas.clear(bounds)
        kanvas.at x, y text value.toString()
    }
}

@CompileStatic
@Bindable
class CounterButton implements KanvasObject {
    Counter counter
    final String text
    final double x, y, w, h
    final int change
    final BoundingBox bounds

    CounterButton(String text, int change, double x, double y, Counter counter) {
        this.text = text
        this.change = change
        this.x = x
        this.y = y
        this.w = 50
        this.h = 50
        this.counter = counter
        bounds = new BoundingBox(x, y, w, h)
    }

    @Override
    void init(Kanvas kanvas) {
        kanvas.mouse.onClick { evt ->
            if (kanvas.mouse.isOn(bounds)) this.counter.value += change
            Unit.INSTANCE
        }
    }

    @Override
    void draw(Kanvas kanvas) {
        kanvas.fill Color.PINK
        kanvas.at x, y rectangle(w, h, true)
        kanvas.at x + 15, y + 35 text(text)
    }
}
