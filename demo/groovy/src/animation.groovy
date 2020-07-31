import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import javafx.scene.paint.Color

import java.time.Duration

@BaseScript KanvasScript baseScript

title 'Groovy Animation Demo'
width 300
height 250

@CompileStatic
class State {
    double x = 30
    double y = 30
    double vx = 5
    double vy = 3
    Color color

    void update(Kanvas k) {
        x += vx
        y += vy
        if (x > k.width - 40 || x < 0) vx *= -1
        if (y > k.height - 40 || y < 0) vy *= -1
        k.fill color
        k.at x, y circle 20, true
    }
}

final state = [
        new State(color: Color.BLUE),
        new State(color: Color.GREEN, x: 100, y: 200),
]

background Color.INDIGO

loopPeriod Duration.ofMillis(15)

loop {
    clear()
    state*.update(kanvas)
}