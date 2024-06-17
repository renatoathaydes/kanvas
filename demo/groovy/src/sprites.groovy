import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.ObservableKanvasObject
import com.athaydes.kanvas.gr.KanvasScript
import groovy.beans.Bindable
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import javafx.geometry.BoundingBox
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.text.Font
import kotlin.Unit
import org.jetbrains.annotations.NotNull

@BaseScript KanvasScript baseScript

title 'Kanvas Sprites'
width 600
height 400

background Color.TRANSPARENT
fontColor Color.BLACK
stroke Color.BLACK, 1.0

@Bindable
@CompileStatic
class Grid extends ObservableKanvasObject {
    boolean gridOn = false

    @Override
    void draw(@NotNull Kanvas kanvas) {
        if (!gridOn) return

        kanvas.stroke Color.GRAY, 0.4D

        20.times { j ->
            def y = j * 50
            kanvas.at 0, y lineTo kanvas.width, y
        }
        20.times { i ->
            def x = i * 50
            kanvas.at x, 0 lineTo x, kanvas.height
        }
    }
}

@Bindable
@CompileStatic
class Fighter extends ObservableKanvasObject {
    // sprites source: https://www.pinterest.com/pin/375417318910631151/
    static final Image img = new File('src/sprites.png').withInputStream { new Image(it) } as Image

    private static final BoundingBox[] spriteBoxes = [
            new BoundingBox(35, 35, 70, 110),
            new BoundingBox(180, 35, 70, 110),
            new BoundingBox(180 + 145, 35, 70, 110),
            new BoundingBox(180 + 145 + 145, 35, 70, 110),
            new BoundingBox(180 + 145 + 145 + 145, 35, 70, 110),
    ]

    final double imgWidth = 864
    final double spriteWidth = 864 / 2
    final double spriteHeight = 576 / 2
    final double imgX = 100
    boolean toLeft = true
    int spriteBoxIndex = 0

    void updateSpriteBox() {
        setSpriteBoxIndex((spriteBoxIndex + 1) % spriteBoxes.size())
    }

    @Override
    void draw(@NotNull Kanvas kanvas) {
        if (toLeft) {
            kanvas.at imgX, 0 image(img, spriteWidth, spriteHeight, spriteBoxes[spriteBoxIndex])
            kanvas.font Font.font(36)
            kanvas.at 30, 50 text 'LEFT'
        } else {
            kanvas.font Font.font(36)
            kanvas.at 30, 50 text 'RIGHT'
            kanvas.withContext { ctx ->
                ctx.scale(-1, 1)
                ctx.translate(-imgWidth, 0)
                kanvas.at(imgWidth - spriteWidth - imgX, 0) image(img, spriteWidth, spriteHeight, spriteBoxes[spriteBoxIndex])
                Unit.INSTANCE
            }
        }
    }
}

@CompileStatic
@TupleConstructor
class SpriteUpdater {
    final Fighter fighter
    private long diff = 0L

    void update(long dt) {
        diff += dt
        if (diff > 180L) {
            fighter.updateSpriteBox()
            diff = 0L
        }
    }
}

def grid = new Grid()
def fighter = new Fighter()
def spriteUpdater = new SpriteUpdater(fighter)

manageKanvasObjects([grid, fighter]) { long dt ->
    spriteUpdater.update(dt)
    if (keyboard.isDown(KeyCode.LEFT) && !fighter.toLeft) {
        fighter.toLeft = true
    } else if (keyboard.isDown(KeyCode.RIGHT) && fighter.toLeft) {
        fighter.toLeft = false
    }
    if (keyboard.isDown(KeyCode.DOWN) && grid.gridOn) {
        grid.gridOn = false
    }
    if (keyboard.isDown(KeyCode.UP) && !grid.gridOn) {
        grid.gridOn = true
    }
    null
}
