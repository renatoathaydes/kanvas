package com.athaydes.kanvas.gr

import groovy.transform.Canonical
import groovy.transform.CompileStatic

import java.time.Duration
import java.util.concurrent.Callable

@CompileStatic
@Canonical
class Looper implements Callable<Object> {
    Duration cycleDuration

    @Delegate
    Callable<? extends Object> action
}
