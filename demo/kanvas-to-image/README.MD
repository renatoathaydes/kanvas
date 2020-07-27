# Kanvas To Image Demo

This demo shows how to save a `Kanvas` object to an image.

It is a simple Gradle project (see the [build.gradle](build.gradle) file).

To run the demo, first create a jar:

```
gradle jar
```

Then, run the jar, passing the location of the image as an argument:

```
java -jar build/libs/kanvas-to-image.jar kanvas.png
```
