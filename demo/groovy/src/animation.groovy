import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.scene.paint.Color

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Demo'
width 300
height 250

def r = 0
def g = 0
def b = 0

def colorComp = { v -> v % 255 }

background Color.WHITESMOKE

loop {
    r = colorComp(r + 1)
    g = colorComp(g + 4)
    b = colorComp(b + 8)
    fill Color.rgb(r, g, b)
    stroke Color.GREEN, 3
    at 50, 30 circle 100, true
}