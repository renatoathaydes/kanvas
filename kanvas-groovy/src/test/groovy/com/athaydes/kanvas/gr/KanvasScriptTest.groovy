package com.athaydes.kanvas.gr

import org.junit.jupiter.api.Test

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

}
