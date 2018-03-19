package com.elpassion.nspek

class NSpekMethodContext(private val finishedTestNames: MutableList<String>,
                         private val testSelector: TestSelector) {
    val names = mutableListOf<String>()

    infix fun String.o(code: NSpekMethodContext.() -> Unit) {
        val testPath = createTestPath()
        if (!finishedTestNames.contains(testPath) && testSelector(testPath)) {
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

    private fun String.createTestPath() = createTestPath(names + this)
}