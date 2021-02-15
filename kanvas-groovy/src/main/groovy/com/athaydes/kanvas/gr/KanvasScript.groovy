package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import com.athaydes.kanvas.Keyboard
import com.athaydes.kanvas.SaveKt
import com.sun.nio.file.SensitivityWatchEventModifier
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import javafx.application.Platform
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import java.nio.file.FileSystems
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

abstract class KanvasScript extends Script {

    @Delegate
    Kanvas kanvas

    @PackageScope
    Closure<?> looper

    Keyboard getKeyboard() { kanvas.keyboard }

    void width(double w) {
        kanvas.canvas.width = w
    }

    void height(double h) {
        kanvas.canvas.height = h
    }

    /**
     * Save the Kanvas to an image file.
     * @param file to store image
     * @param formatName informal name of the format (see {@link javax.imageio.ImageIO}).
     */
    void saveToImage(File file, String formatName) {
        SaveKt.saveToImage(kanvas, file, formatName)
    }

    /**
     * Save the Kanvas to a PNG image file.
     * @param file to store image
     */
    void saveToImage(File file) {
        SaveKt.saveToImage(kanvas, file)
    }

    /**
     * Set an updater function to run in a loop and update the Kanvas.
     *
     * A looper is normally used to create Kanvas animations.
     *
     * @param looper to execute in a loop
     */
    @CompileStatic
    void loop(Closure<?> looper) {
        this.looper = looper
        kanvas.loop { long dt ->
            looper dt
        }
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
            script.absoluteFile.parentFile.toPath()
                    .register(watchService,
                            [ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY] as WatchEvent.Kind[],
                            SensitivityWatchEventModifier.HIGH)
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
        executeAndGetLooper kanvasScript
        return kanvas
    }

    @PackageScope
    Closure<?> executeAndGetLooper(KanvasScript kanvasScript) {
        kanvasScript.kanvas = kanvas
        kanvasScript.run()
        kanvasScript.looper
    }

}