import com.athaydes.kanvas.SaveKt
import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Showcase'
width 600
height 650
font Font.font('', FontWeight.BOLD, 22), Color.WHITE
background Color.web('1d1d1d')

def groupColor = Color.LIMEGREEN
def depColor = Color.CYAN

stroke depColor, 3

def box = { x, y, txt, w = null ->
    at(x + 25, y + 35).text(txt)
    at(x, y).rectangle( w ?: txt.size() * 18, 55)
}

box 240, 30, 'kanvas-core', 190
box 20, 150, 'kanvas-groovy', 210

stroke groupColor

box 420, 150, 'demo', 115
box 30, 300, 'groovy', 130
box 120, 380, 'kotlin', 115
box 160, 460, 'kanvas-reactive', 220
box 350, 540, 'kanvas-to-image', 220

stroke groupColor, 1
at 420, 205 lineTo 160, 300
lineTo 235, 380
lineTo 380, 460
lineTo 570, 540

stroke depColor
at 335, 85 lineTo 150, 150
lineTo 180, 380
lineTo 270, 460
lineTo 460, 540
at 125, 205 lineTo 95, 300

font Font.font('', FontPosture.ITALIC, 18)
at 20, 140 text '(hot reloading)'
at 240, 20 text '(Kotlin DSL)'

at 20, 580 lineTo 50, 580
fontColor depColor
at 60, 585 text 'dependency'

stroke groupColor
at 20, 610 lineTo 50, 610
fontColor groupColor
at 60, 610 text 'grouping'

// change to true to save to image
if (false) {
    Platform.runLater {
        SaveKt.saveToImage(kanvas, new File('kanvas-project.png'))
    }
}