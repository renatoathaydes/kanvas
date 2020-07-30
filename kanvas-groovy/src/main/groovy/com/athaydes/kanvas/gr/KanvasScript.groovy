package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.stage.Stage
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import java.nio.file.FileSystems
import java.nio.file.WatchKey
import java.util.concurrent.*

import static java.nio.file.StandardWatchEventKinds.*

abstract class KanvasScript extends Script {

    @Delegate
    Kanvas kanvas

    StringProperty titleProperty

    @PackageScope
    Closure looper

    void title(String title) {
        titleProperty?.set(title)
    }

    void width(double w) {
        kanvas.canvas.width = w
    }

    void height(double h) {
        kanvas.canvas.height = h
    }

    /**
     * Set a function to run in a loop to update the Kanvas.
     *
     * This method should not be called more than once from a script.
     *
     * @param looper to execute in a loop
     */
    void loop(Closure looper) {
        if (this.looper != null) {
            throw new IllegalStateException('Looper has already been set!')
        }
        this.looper = looper
    }
}

@CompileStatic
class GroovyKanvasApp extends KanvasApp {
    final Kanvas kanvas = new Kanvas(300, 250)
    final CompilerConfiguration config = new CompilerConfiguration(scriptBaseClass: KanvasScript.name).with {
        def importCustomizer = new ImportCustomizer()
        importCustomizer.addImports(KanvasScript.name, BaseScript.name)
        addCompilationCustomizers(importCustomizer)
        it
    }
    final GroovyShell shell = new GroovyShell(this.class.classLoader, config)

    private final StringProperty titleProperty = new SimpleStringProperty()
    private File script
    private ScheduledFuture<?> looper
    private ScheduledExecutorService executorService

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
                        System.err.print "Redrawing... "
                        def time = System.currentTimeMillis()
                        draw()
                        time = System.currentTimeMillis() - time
                        System.err.println "(done in $time ms)"
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
        try {
            def newLooper = executeAndGetLooper kanvasScript
            replaceLooper newLooper
        } catch (e) {
            reportError e
        }
        return kanvas
    }

    @PackageScope
    Closure<?> executeAndGetLooper(KanvasScript kanvasScript) {
        kanvasScript.kanvas = kanvas
        kanvasScript.titleProperty = titleProperty
        kanvasScript.run()
        kanvasScript.looper
    }

    private void replaceLooper(Closure<?> newLooper) {
        looper?.cancel(false)
        if (newLooper != null) {
            def ex = executorService ?: Executors.newSingleThreadScheduledExecutor {
                def t = new Thread(it)
                t.name = 'kanvas-looper'
                t.daemon = true
                t
            }

            looper = ex.scheduleAtFixedRate({
                Platform.runLater {
                    try {
                        newLooper()
                    } catch (e) {
                        reportError e
                    }
                }
            }, 100, 100, TimeUnit.MILLISECONDS)
        }
    }

    private static void reportError(error) {
        System.err.println(error)
    }

}