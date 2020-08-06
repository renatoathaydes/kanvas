import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.geometry.BoundingBox
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.text.Font

@BaseScript KanvasScript baseScript

title 'Kanvas Sprites'
width 600
height 400

background Color.TRANSPARENT
fontColor Color.BLACK
stroke Color.BLACK, 1.0

// sprites source: https://www.pinterest.com/pin/375417318910631151/
def img = new File('src/sprites.png').withInputStream { new Image(it) } as Image

double imgWidth = 864
double spriteWidth = 864 / 2
double spriteHeight = 576 / 2
double imgX = 100

def drawSprite = { toLeft ->
    if (toLeft) {
        at imgX, 0 image(img, spriteWidth, spriteHeight, new BoundingBox(35, 35, 70, 110))
        font Font.font(36)
        at 30, 50 text 'LEFT'
    } else {
        font Font.font(36)
        at 30, 50 text 'RIGHT'
        withContext { ctx ->
            ctx.scale(-1, 1)
            ctx.translate(-imgWidth, 0)
            at(imgWidth - spriteWidth - imgX, 0) image(img, spriteWidth, spriteHeight, new BoundingBox(35, 35, 70, 110))
        }
    }
}

def drawGrid = {
    stroke Color.GRAY, 0.4

    20.times { j ->
        def y = j * 50
        at 0, y lineTo width, y
    }
    20.times { i ->
        def x = i * 50
        at x, 0 lineTo x, height
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
        font Font.font(18)
        at(10, height - 50) text 'Press <- or -> to turn the character, UP/DOWN to show/hide the grid'
        redraw = false
    }
}
