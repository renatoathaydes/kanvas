package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import com.athaydes.kanvas.Scheduler
import com.athaydes.kanvas.TaskId
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import javafx.application.Platform
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import java.nio.file.FileSystems
import java.nio.file.WatchKey
import java.time.Duration

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

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
        final scheduler = new Scheduler()

        final TaskId redrawId = scheduler.add(Duration.ofSeconds(2)) {
            Platform.runLater {
                System.err.print "Redrawing... "
                def time = System.currentTimeMillis()
                draw()
                time = System.currentTimeMillis() - time
                System.err.println "(done in $time ms)"
            }
        }

        def thread = new Thread({
            def watchService = FileSystems.getDefault().newWatchService()
            script.absoluteFile.parentFile.toPath().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
            WatchKey key
            while ((key = watchService.take()) != null) {
                key.pollEvents()
                scheduler.requestExecution(redrawId)
                def valid = key.reset()
                if (!valid) break
            }
        }, 'kanvas-script-watcher')
        thread.daemon = true
        thread.start()
    }

    Kanvas draw() {
        kanvas.reset()
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