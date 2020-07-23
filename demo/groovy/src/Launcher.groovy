import com.athaydes.kanvas.gr.GroovyKanvasApp

class Launcher extends GroovyKanvasApp {
    String scriptLocation = 'src/demo.groovy'

    static void main(String[] args) {
        launch(Launcher)
    }
}
