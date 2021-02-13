/*
 * This is a stand-alone Groovy script that can be used to launch a Kanvas Script,
 * in this case, at the "src/demo.groovy" location relative to this file.
 *
 * This file does not require Gradle. You can run it as long as you have Groovy installed
 * and a JVM which supports JavaFX.
 *
 * If you use SDKMAN!, you can use a JavaFX-ready JVM like "14.0.0.fx-librca".
 */
@Grab('org.jetbrains.kotlin:kotlin-stdlib-jdk8')
@Grab('com.athaydes.kanvas:kanvas-groovy:1.0-SNAPSHOT')
import com.athaydes.kanvas.gr.GroovyKanvasApp
import javafx.application.Application

def script = args ? args[0] : 'showcase'
def dir = new File(getClass().location.file).parentFile
Application.launch(GroovyKanvasApp, "$dir/src/${script}.groovy")
