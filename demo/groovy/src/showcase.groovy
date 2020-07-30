import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Showcase'
width 400
height 450
font Font.font('', FontWeight.BOLD, 22), Color.WHITE
background Color.web('1d1d1d')

at 100, 30 text 'Kanvas Showcase'

fontColor Color.CYAN
at 10, 80 text 'Basic shapes'
stroke Color.CYAN, 3
at 10, 100 circle 20
at 70, 120 lineTo 100, 120
at 120, 110 rectangle 30, 20
at 170, 100 oval 20, 40
at 210, 100 square 40

fontColor Color.GREENYELLOW
fill Color.GREENYELLOW

at 10, 180 text 'Polygons'
at 10, 200 polygon([point(0, 0), point(40, 0), point(0, 50)], true)
at 70, 200 polygon([point(0, 15), point(15, 0), point(30, 0),
         point(45, 15), point(45, 30), point(30, 45),
         point(15, 45), point(0, 30)], true)
at 140, 200 polygon([point(0, 0), point(40, 0), point(40, 50),
         point(0, 50), point(15, 25)], true)
at 200, 200 polygon([point(15, 0), point(55, 0),
                     point(40, 50), point(0, 50)], true)

fontColor Color.YELLOW
stroke Color.YELLOW, 3
fill Color.YELLOW

at 10, 300 text 'Arcs'
at 10, 340 arc 100, 50, false, 90, 90
at 30, 290 arc 100, 50, false, 270, 90
at 140, 330 arc 100, 50, false, 0, 180, ArcType.CHORD
at 250, 310 arc 100, 50, true, 90, 220, ArcType.ROUND

font Font.font('Courier', 14), Color.WHITE
at 10, 440 text 'https://github.com/renatoathaydes/kanvas'
