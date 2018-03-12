package com.elpassion.nspek

class NSpekMethodContext(private val finishedTests: MutableSet<CodeLocation>) {
    val names = mutableListOf<String>()

    infix fun String.o(code: NSpekMethodContext.() -> Unit) {
        if (!finishedTests.contains(currentUserCodeLocation)) {
            names.add(this)
            try {
                code()
                finishedTests.add(currentUserCodeLocation)
                throw TestEnd(codeLocation = currentUserCodeLocation)
            } catch (ex: TestEnd) {
                throw ex
            } catch (ex: Throwable) {
                finishedTests.add(currentUserCodeLocation)
                throw TestEnd(ex, codeLocation = currentUserCodeLocation)
            }
        }
    }
}