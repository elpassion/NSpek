package com.elpassion.mspek

import org.junit.runner.Description

data class CodeLocation(val fileName: String, val lineNumber: Int) {
    override fun toString() = "$fileName:$lineNumber"
}

enum class TestState { STARTED, SUCCESS, FAILURE }

data class TestInfo(
        val name: String? = null,
        val location: CodeLocation? = null,
        val state: TestState? = null,
        val failureLocation: CodeLocation? = null,
        val failureCause: Throwable? = null,
        val description: Description? = null
)

data class TestTree(
        var info: TestInfo = TestInfo(),
        val subtrees: MutableList<TestTree> = mutableListOf()
)

fun TestTree.reset(i: TestInfo = TestInfo()) {
    info = i
    subtrees.clear()
}

class TestEnd(cause: Throwable? = null) : RuntimeException(cause)
