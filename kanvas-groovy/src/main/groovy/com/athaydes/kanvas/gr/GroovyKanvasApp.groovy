package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import com.sun.nio.file.SensitivityWatchEventModifier
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import javafx.application.Platform
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

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
                        System.err.print "Recompiling... "
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
                            [StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY] as WatchEvent.Kind[],
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