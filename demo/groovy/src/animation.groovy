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
class KanvasCircle {
    double x = 30
    double y = 30
    double vx = 0.3
    double vy = 0.2
    final double diameter = 40
    Color color

    void update(Kanvas k, long dt) {
        x += vx * dt
        y += vy * dt
        // keep the circle within the canvas
        if (x + diameter > k.width || x < 0) {
            vx *= -1
            x = x < 0 ? 0 : k.width - diameter
        }
        if (y + diameter > k.height || y < 0) {
            vy *= -1
            y = y < 0 ? 0 : k.height - diameter
        }
        k.fill color
        k.at x, y circle((diameter / 2.0).doubleValue(), true)
    }
}

final circles = [
        new KanvasCircle(color: Color.BLUE),
        new KanvasCircle(color: Color.GREEN, x: 100, y: 140),
]

background Color.INDIGO

loopPeriod Duration.ofMillis(15)

loop { long dt ->
    clear()
    circles*.update(kanvas, dt)
}
