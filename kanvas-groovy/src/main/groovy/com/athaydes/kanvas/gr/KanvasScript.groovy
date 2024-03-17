package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.Keyboard
import com.athaydes.kanvas.SaveKt
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import kotlin.Unit

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
            Unit.INSTANCE
        }
    }

    @CompileStatic
    void kanvasObjects(KanvasObject... objects) {
        managePogos objects.collect { obj ->
            new PogoWrapper(obj)
        }
    }

    @CompileStatic
    private void managePogos(List<PogoWrapper> pogos) {
        for (pogo in pogos) {
            pogo.init(kanvas)
            watchForChangesIn pogo
        }

        loop { long dt ->
            for (pogo in pogos) {
                pogo.update(kanvas, dt)
                if (pogo.isDirty.get()) {
                    pogo.draw(kanvas)
                    pogo.isDirty.set false
                }
            }
        }
    }

    @CompileStatic
    private void watchForChangesIn(PogoWrapper pogo) {
        PropertyChangeListener listener = { PropertyChangeEvent evt ->
            pogo.isDirty.set true
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
