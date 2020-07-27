# Kanvas Reactive Demo

This is a demo showing how a reactive Kanvas can be easily created by using a good reactive programming library,
like [ReduxKotlin](https://reduxkotlin.org/) in this example.

A data model is defined in the [src/model.kt](src/model.kt) file, while the `Launcher` and setup code can be found
in [src/reactive.kt](src/reactive.kt).

## Running the demo

To execute the launcher, you can create a jar first:

```
gradle jar
```

Then, run the jar:

```
java -jar build/libs/kanvas-reactive-demo.jar
```

![Kanvas Reactive Demo](../screenshots/kanvas-reactive.gif)
