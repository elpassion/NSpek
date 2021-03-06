package com.elpassion.nspek

val currentUserCodeLocation get() = Thread.currentThread().stackTrace.userCodeLocation

val StackTraceElement.location get() = CodeLocation(fileName, lineNumber)

val Throwable.causeLocation: CodeLocation?
    get() {
        val file = stackTrace.userCodeFrame.fileName
        val frame = stackTrace?.firstOrNull { it.fileName == file }
        return frame?.location
    }

val Array<StackTraceElement>.userCodeLocation: CodeLocation
    get() {
        return userCodeFrame.location
    }

private val Array<StackTraceElement>.userCodeFrame: StackTraceElement
    get() {
        return get(indexOfFirst { it.methodName == "o" && it.className == NSpekMethodContext::class.java.canonicalName } + 1)
    }

fun createTestPath(testNames:List<String>): String = testNames.joinToString("|")