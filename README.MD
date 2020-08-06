# Kanvas

This project contains a few libraries to make it easy to draw on a JavaFX Canvas using
either a Kotlin or Groovy DSL.

## Libraries

_Diagram produced by Kanvas itself!_

![Kanvas Project Diagram](demo/screenshots/kanvas-project.png)

### kanvas-core

> Maven coordinates: `com.athaydes.kanvas:kanvas-core:<version>`

The basic functionality of Kanvas. It's written in Kotlin and provides the basic API for drawing on a canvas
via the [com.athaydes.kanvas.Kanvas](kanvas-core/src/main/kotlin/com/athaydes/kanvas/kanvas.kt) class.

This library also provides an abstract class, `com.athaydes.kanvas.KanvasApp`, to make it easy to create a
`javafx.application.Application` implementation that displays a `Kanvas` and nothing else:

```kotlin
import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import javafx.application.Application
import javafx.scene.paint.Color

fun main() {
    Application.launch(KanvasDemo::class.java)
}

class KanvasDemo : KanvasApp() {
    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            title("Kotlin Kanvas Demo")

            // draw your shapes here!
            background(Color.BLACK).fill(Color.BLUE)
            at(50.0, 30.0).circle(radius = 100.0, fill = true)
        }
    }
}
```

Result:

![Kotlin Basic Screenshot](demo/screenshots/basic.png)

### Animations

If you want to create animations rather than static images, you can use the `loop` function as in the example below:

> To change the frame rate, use the `loopPeriod` function, which accepts a number (milliseconds) or `java.time.Duration`.

```kotlin
import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.KanvasApp
import javafx.scene.paint.Color

class AnimationKanvasDemo : KanvasApp() {
    data class State(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var vx: Double = 0.3,
        var vy: Double = 0.2,
        var color: Color = Color.BLACK
    ) {
        fun update(k: Kanvas, dt: Long) {
            x += vx * dt
            y += vy * dt
            if (x > k.width - 40 || x < 0) vx *= -1
            if (y > k.height - 40 || y < 0) vy *= -1
            k.fill(color).at(x, y).circle(radius = 20.0, fill = true)
        }
    }

    val state = listOf(
        State(color = Color.BLUE),
        State(color = Color.GREEN, x = 100.0, y = 140.0)
    )

    override fun draw(): Kanvas {
        return Kanvas(300.0, 250.0).apply {
            title("Kanvas Animation Demo")
            background(Color.INDIGO)
            loop { dt ->
                clear()
                state.forEach { s -> s.update(this, dt) }
            }
        }
    }
}
```

Result:

![Kotlin Animation](demo/screenshots/kanvas-animation.gif)

## kanvas-groovy

> Maven coordinates: `com.athaydes.kanvas:kanvas-groovy:<version>`

A Groovy DSL based on `kanvas-core`.

It provides a base class for Groovy scripts exposing a nice DSL for Kanvas:

```groovy
import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import javafx.scene.paint.Color

@BaseScript KanvasScript baseScript

title 'Groovy Kanvas Demo'
width 300
height 250

background Color.BLACK
fill Color.BLUE
at 50, 30 circle 100, true
```

Animation:

> To make sure your animations run smoothly, it's a good idea to use `@CompileStatic`
> in the method or class that draws on the Kanvas, as in the example below.

```groovy
import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.gr.KanvasScript
import groovy.transform.BaseScript
import groovy.transform.CompileStatic
import javafx.scene.paint.Color

import java.time.Duration

@BaseScript KanvasScript baseScript

title 'Groovy Animation Demo'
width 300
height 250

@CompileStatic
class State {
    double x = 30
    double y = 30
    double vx = 0.3
    double vy = 0.2
    Color color

    void update(Kanvas k, long dt) {
        x += vx * dt
        y += vy * dt
        if (x > k.width - 40 || x < 0) vx *= -1
        if (y > k.height - 40 || y < 0) vy *= -1
        k.fill color
        k.at x, y circle 20, true
    }
}

final state = [
        new State(color: Color.BLUE),
        new State(color: Color.GREEN, x: 100, y: 140),
]

background Color.INDIGO

loopPeriod Duration.ofMillis(15)

loop { long dt ->
    clear()
    state*.update(kanvas, dt)
}
```

_Both Groovy examples produce equivalent results as the Kotlin examples shown earlier._

#### Hot reloading Groovy scripts

This library also provides a JavaFX application class, `com.athaydes.kanvas.gr.GroovyKanvasApp`, that can launch a
Groovy script and hot-reload it as it changes.

All files under the same directory tree as the Groovy script are watched.

For details on how to run Kanvas Groovy scripts, see the [Groovy Demo](demo/groovy/).

## Demos

[Kotlin Demo](demo/kotlin/)

Contains a basic example as well as a simple animation.

[Groovy Demo](demo/groovy/)

Includes several Groovy scripts demonstrating Kanvas created with kanvas-groovy. The scripts can be launched via a
stand-alone Groovy launcher or from a normal jar built from a Gradle project.

* [kanvas-project-diagram.groovy](demo/groovy/src/kanvas-project-diagram.groovy) (the diagram shown at the top of this page)
* [animation.groovy](demo/groovy/src/animation.groovy) (simple animation)
* [sprites.groovy](demo/groovy/src/sprites.groovy) (shows keyboard event handling, usage of sprites and transformations)
* [showcase.groovy](demo/groovy/src/showcase.groovy) (shown below)

![Showcase](demo/screenshots/showcase.png)

[Kanvas-Reactive](demo/kanvas-reactive/)

A Kotlin example that shows how [ReduxKotlin](https://reduxkotlin.org/) can be used to create a reactive JavaFX 
application that uses Kanvas.

![Kanvas Reactive Demo](demo/screenshots/kanvas-reactive.gif)

[Kanvas-To-Image](demo/kanvas-to-image/)

Another Kotlin example that shows how to save a Kanvas as an image file (e.g. PNG).

## Development

To install the current version of the library in your local maven cache, use

    ./gradlew install
    
Since this library uses JavaFX, make sure that you use a jdk with JavaFX! For example, using [SDKMAN!](https://sdkman.io/):

    sdk install java 11.0.8.fx-zulu
    

