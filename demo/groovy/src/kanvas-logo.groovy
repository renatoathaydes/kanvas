import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Demo'
width 340
height 130

background Color.BLACK
fill Color.BLUE

at 20, 20 polygon([
        point(0, 0), point(60, 0), point(20, 30),
        point(60, 60), point(0, 60)
], true)

fill Color.YELLOW
withContext { ctx ->
    ctx.with {
        rotate(30)
        translate(-20, -60)
    }
    at 90, 20 triangle(point(0, 60), point(30, 0), point(60, 60), true)
}

fill Color.CYAN
at 110, 20 polygon([
        point(0, 60), point(15, 0), point(40, 55), point(60, 0), point(60, 60)
], true)

fill Color.GREEN
at 175, 20 triangle(point(0, 0), point(30, 60), point(60, 0), true)

fill Color.YELLOW
at 220, 20 triangle(point(0, 60), point(30, 0), point(60, 60), true)

stroke Color.RED, 10, StrokeLineCap.ROUND
at 290, 50 lineTo 320, 20 lineTo 320, 50
at 290, 80 lineTo 320, 50

if (false) {
    saveToImage(new File('kanvas-logo.png'))
}