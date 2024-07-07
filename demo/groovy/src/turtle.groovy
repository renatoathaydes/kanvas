import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.scene.paint.Color

@BaseScript KanvasScript baseScript

background Color.WHITE
title 'Logo Turtle'
width 400
height 200

at 200, 100

withTurtle { t ->
    def polygon = { int sides ->
        sides.times {
            t.move(15).rotate(360 / sides)
        }
    }
    def colors = [Color.BLACK, Color.RED, Color.BROWN, Color.BLUE, Color.GREEN, Color.MAGENTA]
    colors.each { color ->
        t.color(color)
        polygon 10
        t.rotate(60)
    }
}

saveToImage(new File('flower.png'))
