package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.Keyboard
import com.athaydes.kanvas.ObservableKanvasObject
import com.athaydes.kanvas.SaveKt
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

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
     * Set whether the Scene should be resizable.
     *
     * This only has effect if called before the Kanvas initial drawing.
     * @param isResizable
     */
    void resizable(boolean isResizable) {
        kanvas.resizable = isResizable
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
     * Set the objects that should be managed by Kanvas to be redrawn as necessary.
     *
     * All {@link ObservableKanvasObject}s are monitored for changes. When any object changes,
     * a full redraw of the canvas is performed.
     *
     * This method should only be called once.
     *
     * All given objects should have the {@link groovy.beans.Bindable} annotation,
     * which is a replacement for Kotlin delegate properties as using them is not easy to do from Groovy/Java.
     */
    void manageKanvasObjects(ObservableKanvasObject... objects) {
        for (object in objects) {
            watchForChangesIn(new ObservablePropertySupport(object))
        }
        kanvas.manageKanvasObjects(objects)
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

    @CompileStatic
    private void watchForChangesIn(ObservablePropertySupport pogo) {
        PropertyChangeListener listener = { PropertyChangeEvent evt ->
            pogo.changed = true
        }
        pogo.addPropertyChangeListener(listener)
        watchForChangesInNestedProperties(pogo.pogo, listener)
    }

    private void watchForChangesInNestedProperties(object, PropertyChangeListener listener) {
        for (prop in object.properties.keySet()) {
            def nestedObject = object[prop]
            if (nestedObject.respondsTo('addPropertyChangeListener')) {
                try {
                    nestedObject.addPropertyChangeListener(listener)
                    watchForChangesInNestedProperties(nestedObject, listener)
                } catch (ignored) {
                }
            }
        }
    }
}
