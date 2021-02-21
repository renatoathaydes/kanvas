package com.athaydes.kanvas.gr

import com.athaydes.kanvas.Kanvas
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.beans.PropertyChangeListener
import java.util.concurrent.atomic.AtomicBoolean

interface KanvasObject {
    default void init(Kanvas kanvas) {
    }

    default void update(Kanvas kanvas, long dt) {
    }

    void draw(Kanvas kanvas)
}

@Canonical
@PackageScope
class PogoWrapper implements KanvasObject {

     final KanvasObject pogo

    final AtomicBoolean isDirty = new AtomicBoolean( true )

    PogoWrapper(KanvasObject pogo) {
        this.pogo = pogo
    }

    @CompileStatic
    @Override
    void init(Kanvas kanvas) {
        pogo.init(kanvas)
    }

    @CompileStatic
    @Override
    void update(Kanvas kanvas, long dt) {
        pogo.update(kanvas, dt)
    }

    @CompileStatic
    @Override
    void draw(Kanvas kanvas) {
        pogo.draw(kanvas)
    }

    void addPropertyChangeListener(PropertyChangeListener listener) {
        pogo.addPropertyChangeListener(listener)
    }

    void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        pogo.addPropertyChangeListener(name, listener)
    }

    void removePropertyChangeListener(PropertyChangeListener listener) {
        pogo.removePropertyChangeListener(listener)
    }

    void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        pogo.removePropertyChangeListener(name, listener)
    }

    void firePropertyChange(String name, oldValue, newValue) {
        pogo.firePropertyChange(name, oldValue, newValue)
    }

    PropertyChangeListener[] getPropertyChangeListeners() {
        pogo.getPropertyChangeListeners()
    }

    PropertyChangeListener[] getPropertyChangeListeners(String name) {
        pogo.getPropertyChangeListeners()
    }

}