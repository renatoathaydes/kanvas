import com.athaydes.kanvas.gr.GroovyKanvasApp

class Launcher extends GroovyKanvasApp {
    String getScriptLocation() {
        parameters.raw.isEmpty()
                ? 'src/demo.groovy'
                : parameters.raw[0]
    }

    static void main(String[] args) {
        launch(Launcher, args)
    }
}
