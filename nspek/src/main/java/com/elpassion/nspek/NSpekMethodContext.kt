package com.elpassion.nspek

class NSpekMethodContext(private val finishedTestNames: MutableList<String>) {
    val names = mutableListOf<String>()

    infix fun String.o(code: NSpekMethodContext.() -> Unit) {
        if (!finishedTestNames.contains((names + this).joinToString())) {
            names.add(this)
            try {
                code()
                throw TestEnd(codeLocation = currentUserCodeLocation)
            } catch (ex: TestEnd) {
                throw ex
            } catch (ex: Throwable) {
                throw TestEnd(cause = ex, codeLocation = currentUserCodeLocation)
            }
        }
    }
}