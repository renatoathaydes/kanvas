import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.scene.paint.Color

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Demo'
width 300
height 250

background Color.BLACK
fill Color.BLUE
stroke Color.GREEN, 3
at 50, 30 circle 100, true
