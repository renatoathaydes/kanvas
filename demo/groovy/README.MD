# Groovy Kanvas Demo

This demo shows how to use the `kanvas-groovy` library in 2 different ways.

* with a stand-alone Groovy script launcher.
* in a standard Gradle application.

## Stand-alone Groovy script launcher

The stand-alone Groovy script launcher is right next to this file: [launcher.groovy](launcher.groovy).

It launches the Kanvas script at [src/demo.groovy](src/demo.groovy).

Using Groovy grapes, you don't even need a build system, just a single Groovy file.

Here's the most basic launcher script:

```groovy
@Grab('org.jetbrains.kotlin:kotlin-stdlib-jdk8')
@Grab('com.athaydes.kanvas:kanvas-groovy:1.0-SNAPSHOT')
import com.athaydes.kanvas.gr.GroovyKanvasApp
import javafx.application.Application

Application.launch(GroovyKanvasApp, "script.groovy")
```

This will launch your Kanvas script at `script.groovy` in the working directory.

> Notice that kanvas-groovy will automatically hot-reload your Kanvas script on every change you make!

## Gradle Application

If you want to build a standard Gradle application, then you simply create a Gradle project with Groovy support
and a simple main class extending from `com.athaydes.kanvas.gr.GroovyKanvasApp`.

The folder where this file is located is, in fact, a Gradle project.

Check out the [build.gradle](build.gradle) and the launcher at [src/Launcher.groovy](src/Launcher.groovy).

A basic Kanvas launcher should look like this:

```groovy
import com.athaydes.kanvas.gr.GroovyKanvasApp

class Launcher extends GroovyKanvasApp {
    String scriptLocation = 'script.groovy'

    static void main(String[] args) {
        launch(Launcher)
    }
}
```

This example also hot-reloads when `script.groovy` changes!

To execute the launcher, you can create a jar first:

```
gradle jar
```

Then, run the jar:

```
java -jar build/libs/kanvas-groovy-demo.jar
```

To run a different Kanvas script, pass it as an argument to the program as shown in the _Showcase_ section below.

## Showcase

This project includes a showcase Groovy script at [src/showcase.groovy](src/showcase.groovy) which includes examples of
using all available functions in `KanvasScript`.

To launch it, you can use the jar created in the previous section:

```
java -jar build/libs/kanvas-groovy-demo.jar src/showcase.groovy
```

You should see this:

![Kanvas Showcase](../screenshots/showcase.png)

## Diagram

There is also a diagram produced by [src/kanvas-project-diagram.groovy](src/kanvas-project-diagram.groovy) which shows
the organization of the Kanvas Project itself:

![Kanvas Project Diagram](../screenshots/kanvas-project.png)
