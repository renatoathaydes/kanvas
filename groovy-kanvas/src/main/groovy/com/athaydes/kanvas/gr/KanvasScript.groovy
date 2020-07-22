package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.application.Platform
import org.codehaus.groovy.control.CompilerConfiguration

import java.nio.file.FileSystems
import java.nio.file.WatchKey
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

import static java.nio.file.StandardWatchEventKinds.*

abstract class KanvasScript extends DelegatingScript {

    @Delegate
    Kanvas kanvas

    void width(double w) {
        kanvas.canvas.width = w
    }

    void height(double h) {
        kanvas.canvas.height = h
    }

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
        final redrawQueue = new LinkedBlockingDeque(1)
        def drawBounceThread = new Thread({
            while (true) {
                def next = redrawQueue.poll(2, TimeUnit.SECONDS)
                if (next) {
                    // wait until no requests are added within 100ms
                    while (redrawQueue.poll(100, TimeUnit.MILLISECONDS)) {
                    }
                    Platform.runLater {
                        println "Redrawing"
                        draw()
                    }
                }
            }
        })
        drawBounceThread.daemon = true
        drawBounceThread.start()

        def thread = new Thread({
            def watchService = FileSystems.getDefault().newWatchService()
            script.parentFile.toPath().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
            WatchKey key
            while ((key = watchService.take()) != null) {
                key.pollEvents()
                redrawQueue.offer(true)
                def valid = key.reset()
                if (!valid) break
            }
        }, 'kanvas-script-watcher')
        thread.daemon = true
        thread.start()
    }

    Kanvas draw() {
        kanvas.clear()
        def kanvasScript = shell.parse(script) as KanvasScript
        kanvasScript.kanvas = kanvas
        kanvasScript.delegate = kanvas
        kanvasScript.run()
        return kanvas
    }
}