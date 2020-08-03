# Kotlin Kanvas Demo

This is the Kolin demo for the Kanvas Project.

It is a simple Gradle project (see the [build.gradle](build.gradle) file).

To run the demo, first create a jar:

```
gradle jar
```

Then, run the jar:

```
java -jar build/libs/kanvas-demo.jar
```

By default, the [basic.kt](src/basic.kt) application is run. To run the [animation.kt](src/animation.kt) demo, 
pass `animation` as an argument:

```
java -jar build/libs/kanvas-demo.jar animation
```

