import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Showcase'
width 600
height 500
font Font.font('', FontWeight.BOLD, 22), Color.BLACK
background Color.web('99ddff')

at 100, 30 text 'Kanvas Showcase'

fontColor Color.BLUE
at 10, 80 text 'Basic shapes'
stroke Color.BLUE, 3
at 10, 100 circle 20
at 70, 120 lineTo 100, 120
at 120, 110 rectangle 30, 20
at 170, 100 oval 20, 40

fontColor Color.GREEN
fill Color.GREEN

at 10, 180 text 'Polygons'
polygon([point(10, 200), point(50, 200), point(10, 250)], true)