import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.scene.paint.Color

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Demo'
width 300
height 200

background Color.web('99ddff')

stroke Color.BLUE, 3
at 50, 50 circle 20

fill Color.YELLOW
at 100, 50 circle 30, true