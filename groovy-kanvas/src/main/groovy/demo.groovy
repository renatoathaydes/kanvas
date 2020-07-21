import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.scene.paint.Color

@BaseScript KanvasScript baseScript

background Color.web('blue')

stroke Color.RED, 3
at 50, 50 circle 20, false

fill Color.WHITE
at 100, 50 circle 30, true
