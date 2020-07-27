import com.athaydes.kanvas.Kanvas
import javafx.scene.paint.Color
import javafx.scene.text.Font

data class MyState(
    val text: String,
    val background: Color,
    val foreground: Color
) : State {
    override fun draw(kanvas: Kanvas) {
        kanvas.background(background)
            .font(Font.font("", 48.0), foreground)
            .at(10.0, 40.0).text(text)
    }
}

sealed class MyAction {
    abstract fun update(state: MyState): MyState

    data class ChangeBackground(val color: Color) : MyAction() {
        override fun update(state: MyState) = state.copy(background = color)

    }

    data class ChangeForeground(val color: Color) : MyAction() {
        override fun update(state: MyState) = state.copy(foreground = color)
    }

    data class ChangeText(val text: String) : MyAction() {
        override fun update(state: MyState) = state.copy(text = text)
    }
}
