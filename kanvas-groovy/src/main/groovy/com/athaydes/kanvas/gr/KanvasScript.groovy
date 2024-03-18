package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import com.athaydes.kanvas.Keyboard
import com.athaydes.kanvas.ObservableKanvasObject
import com.athaydes.kanvas.SaveKt
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.lang.System.Logger

@CompileStatic
abstract class KanvasScript extends Script {

    private static final Logger log = System.getLogger(KanvasScript.name)

    @Delegate
    Kanvas kanvas

    @PackageScope
    Closure<?> looper

    Keyboard getKeyboard() { kanvas.keyboard }

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
    void loop(Closure<?> looper) {
        this.looper = looper
        kanvas.loop { long dt ->
            looper dt
        }
    }

    private void watchForChangesIn(ObservablePropertySupport pogo) {
        PropertyChangeListener listener = { PropertyChangeEvent evt ->
            log.log Logger.Level.DEBUG, "Property '{0}' changed from '{1}' to '{2}'",
                    evt.propertyName, evt.oldValue, evt.newValue
            pogo.changed = true
        }
        pogo.addPropertyChangeListener(listener)
        watchForChangesInNestedProperties(pogo.pogo, listener)
    }

    @CompileDynamic
    private void watchForChangesInNestedProperties(object, PropertyChangeListener listener) {
        def skipList = ['class', 'propertyChangeListeners', 'metaClass']
        for (prop in object.properties.keySet()) {
            if (prop in skipList) continue
            def nestedObject = object[prop]
            if (nestedObject.respondsTo('addPropertyChangeListener')) {
                log.log Logger.Level.TRACE, "Adding listener to property: {0} of {1}", prop, object
                try {
                    nestedObject.addPropertyChangeListener(listener)
                    watchForChangesInNestedProperties(nestedObject, listener)
                } catch (e) {
                    log.log(Logger.Level.DEBUG, "Error adding propertyChangeListener to {0}: {1}",
                            nestedObject, e)
                }
            }
        }
    }
}
