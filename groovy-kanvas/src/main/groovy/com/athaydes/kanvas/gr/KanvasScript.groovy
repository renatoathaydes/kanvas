package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.application.Platform
import org.codehaus.groovy.control.CompilerConfiguration

abstract class KanvasScript extends DelegatingScript {

    @Delegate
    Kanvas kanvas

    static void main(String[] args) {
        Application.launch(KanvasDemo)
    }
}

@CompileStatic
class KanvasDemo extends KanvasApp {
    final Kanvas kanvas = new Kanvas(300, 250)
    final File script = new File('groovy-kanvas/src/main/groovy/demo.groovy')
    final CompilerConfiguration config = new CompilerConfiguration(scriptBaseClass: KanvasScript.name)
    final GroovyShell shell = new GroovyShell(this.class.classLoader, config)

    KanvasDemo() {
        Thread.start {
            def modified = script.lastModified()
            while (true) {
                sleep 2000
                def lastModified = script.lastModified()
                if (modified != lastModified) {
                    println "Drawing again"
                    Platform.runLater { draw() }
                    modified = lastModified
                }
            }
        }
    }

    Kanvas draw() {
        kanvas.clear()
        def kanvasScript = shell.parse(script) as KanvasScript
        kanvasScript.kanvas = kanvas
        kanvasScript.run()
        return kanvas
    }
}