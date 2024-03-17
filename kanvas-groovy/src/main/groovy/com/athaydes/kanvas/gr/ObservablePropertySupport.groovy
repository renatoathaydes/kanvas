package com.athaydes.kanvas.gr

import com.athaydes.kanvas.ObservableKanvasObject
import groovy.transform.Canonical
import groovy.transform.PackageScope

import java.beans.PropertyChangeListener

@Canonical
@PackageScope
class ObservablePropertySupport extends ObservableKanvasObject {

    @Delegate
    final ObservableKanvasObject pogo

    ObservablePropertySupport(ObservableKanvasObject pogo) {
        this.pogo = pogo
    }

    void setChanged(boolean changed) {
        pogo.isChanged().set(changed)
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