import com.athaydes.kanvas.Kanvas
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.reduxkotlin.Store
import org.reduxkotlin.createStore

interface State {
    fun draw(kanvas: Kanvas)
}

class KanvasReactive(val store: Store<out State>) {

    fun start(width: Double, height: Double): Node {
        val kanvas = Kanvas(width, height)
        store.subscribe {
            Platform.runLater {
                kanvas.clear()
                store.state.draw(kanvas)
            }
        }
        Platform.runLater { store.state.draw(kanvas) }
        return kanvas.node
    }

}

class Example : Application() {
    private fun reducer(state: MyState, action: Any): MyState =
        if (action is MyAction) action.update(state) else state

    val store: Store<MyState> =
        createStore(::reducer, MyState("Kanvas Reactive!", background = Color.BLACK, foreground = Color.WHITE))

    override fun start(primaryStage: Stage) {
        val state = store.state
        val bkgPicker = ColorPicker(state.background)
        bkgPicker.setOnAction { _ ->
            store.dispatch(MyAction.ChangeBackground(bkgPicker.value))
        }

        val frgPicker = ColorPicker(state.foreground)
        frgPicker.setOnAction { _ ->
            store.dispatch(MyAction.ChangeForeground(frgPicker.value))
        }

        val textField = TextArea(state.text)
        textField.prefRowCount = 3
        textField.setOnKeyReleased { _ ->
            store.dispatch(MyAction.ChangeText(textField.text))
        }

        val pickers = VBox(Label("Background:"), bkgPicker, Label("Foreground:"), frgPicker)
        pickers.spacing = 10.0

        val canvas = KanvasReactive(store).start(400.0, 200.0)

        val canvasPane = GridPane()
        canvasPane.children.add(canvas)
        GridPane.setMargin(canvas, Insets(20.0, 0.0, 10.0, 0.0))

        val root = BorderPane(canvasPane, pickers, null, textField, null)
        root.padding = Insets(10.0)

        primaryStage.title = "Kanvas-Reactive Demo"
        primaryStage.scene = Scene(root, 450.0, 450.0)
        primaryStage.centerOnScreen()
        primaryStage.show()
    }

}

object Launcher {
    @JvmStatic
    fun main(vararg args: String) {
        Application.launch(Example::class.java)
    }
}