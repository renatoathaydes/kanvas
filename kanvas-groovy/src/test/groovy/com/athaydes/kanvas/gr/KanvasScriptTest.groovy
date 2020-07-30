package com.athaydes.kanvas.gr


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows

class KanvasScriptTest {

    @Test
    void 'can setup function to update canvas in loop'() {
        def app = new GroovyKanvasApp()
        def script = app.shell.parse('''
        loop {
            42
        }
        ''') as KanvasScript

        def looper = app.executeAndGetLooper(script)
        assert looper() == 42
    }

    @Test
    void 'cannot set function to update canvas in loop more than once'() {
        def app = new GroovyKanvasApp()
        def script = app.shell.parse('''
        loop { 42 }
        loop { 43 }
        ''') as KanvasScript

        assertThrows(IllegalStateException) { app.executeAndGetLooper(script) }
    }
}
