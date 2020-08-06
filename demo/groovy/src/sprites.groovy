import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.geometry.BoundingBox
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.text.Font

@BaseScript KanvasScript baseScript

title 'Kanvas Sprites'
width 1000
height 800

background Color.TRANSPARENT
fontColor Color.BLACK
stroke Color.BLACK, 1.0

// sprites source: https://www.pinterest.com/pin/375417318910631151/
def img = new File('src/sprites.png').withInputStream { new Image(it) } as Image

def imgWidth = 864
def spriteWidth = 864
def spriteHeight = 576

def drawSprite = { toLeft ->
    if (toLeft) {
        at 0, 0 image(img, spriteWidth, spriteHeight, new BoundingBox(35, 35, 70, 110))
        font Font.font(36)
        at 30, 50 text 'LEFT'
    } else {
        font Font.font(36)
        at 30, 50 text 'RIGHT'
        withContext { ctx ->
            ctx.scale(-1, 1)
            ctx.translate(-imgWidth, 0)
            at(imgWidth - spriteWidth, 0) image(img, spriteWidth, spriteHeight, new BoundingBox(35, 35, 70, 110))
        }
    }
}

def drawGrid = {
    stroke Color.GRAY, 0.4

    20.times { j ->
        def y = j * 50
        at 0, y lineTo 1000, y
    }
    20.times { i ->
        def x = i * 50
        at x, 0 lineTo x, 1000
    }
}

toLeft = true
gridOn = false
redraw = true

loop {
    if (keyboard.isDown(KeyCode.LEFT) && !toLeft) {
        toLeft = true
        redraw = true
    } else if (keyboard.isDown(KeyCode.RIGHT) && toLeft) {
        toLeft = false
        redraw = true
    }
    if (keyboard.isDown(KeyCode.DOWN) && gridOn) {
        gridOn = false
        redraw = true
    }
    if (keyboard.isDown(KeyCode.UP) && !gridOn) {
        gridOn = true
        redraw = true
    }
    if (redraw) {
        clear()
        drawSprite(toLeft)
        if (gridOn) drawGrid()
        font Font.font(24)
        at 30, 700 text 'Press <- or -> to turn the character, UP/DOWN to show/hide the grid'
        redraw = false
    }
}
