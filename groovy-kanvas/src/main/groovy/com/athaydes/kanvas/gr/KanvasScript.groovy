package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.stage.Stage
import org.codehaus.groovy.control.CompilerConfiguration

import java.nio.file.FileSystems
import java.nio.file.WatchKey
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

import static java.nio.file.StandardWatchEventKinds.*

abstract class KanvasScript extends DelegatingScript {

    @Delegate
    Kanvas kanvas

    StringProperty titleProperty

    void title(String title) {
        titleProperty?.set(title)
    }

    void width(double w) {
        kanvas.canvas.width = w
    }

    void height(double h) {
        kanvas.canvas.height = h
    }
}

@CompileStatic
class GroovyKanvasApp extends KanvasApp {
    final Kanvas kanvas = new Kanvas(300, 250)
    final CompilerConfiguration config = new CompilerConfiguration(scriptBaseClass: KanvasScript.name)
    final GroovyShell shell = new GroovyShell(this.class.classLoader, config)

    private final StringProperty titleProperty = new SimpleStringProperty()
    private File script

    String getScriptLocation() {
        def args = getParameters().raw
        if (args.size() == 1) {
            return args[0]
        } else {
            throw new RuntimeException("Expected a single argument, the Kanvas Script file, but got $args")
        }
    }

    @Override
    void init() {
        script = new File(getScriptLocation())
        if (!script.file) {
            throw new FileNotFoundException(script.absolutePath)
        }

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
            script.absoluteFile.parentFile.toPath().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
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

    @Override
    void start(Stage primaryStage) {
        primaryStage.titleProperty().bind(titleProperty)
        super.start(primaryStage)
    }

    Kanvas draw() {
        kanvas.clear()
        def kanvasScript = shell.parse(script) as KanvasScript
        kanvasScript.kanvas = kanvas
        kanvasScript.delegate = kanvas
        kanvasScript.titleProperty = titleProperty
        try {
            kanvasScript.run()
        } catch (e) {
            System.err.println(e)
        }
        return kanvas
    }

}