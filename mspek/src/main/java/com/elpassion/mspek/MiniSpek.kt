package com.elpassion.mspek

object MiniSpek {

    private val finishedTests: MutableMap<CodeLocation, Throwable> = mutableMapOf()

    var log: MLog = ::logToConsole

    fun mspek(name: String, code: () -> Unit) {
        finishedTests.clear()
        log.start(name, currentUserCodeLocation)
        while (true) {
            try {
                code()
                return
            } catch (e: TestEnd) {
                val location = e.stackTrace[1].location
                finishedTests[location] = e
                log.end(location, e)
            }
        }
    }

    infix fun String.o(code: () -> Unit) {
        if (currentUserCodeLocation !in finishedTests) {
            throw try {
                log.start(this, currentUserCodeLocation)
                code()
                TestEnd()
            } catch (e: TestEnd) {
                e
            } catch (e: Throwable) {
                TestEnd(e)
            }
        }
    }
}
