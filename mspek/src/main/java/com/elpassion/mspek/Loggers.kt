package com.elpassion.mspek

import com.elpassion.mspek.TestState.*

typealias MLog = (TestInfo) -> Unit

fun MLog.start(name: String, location: CodeLocation) = this(TestInfo(name, location, STARTED))

fun MLog.end(location: CodeLocation, exception: TestEnd) = this(TestInfo(
        location = location,
        state = if (exception.cause === null) SUCCESS else FAILURE,
        failureLocation = exception.causeLocation,
        failureCause = exception.cause
))

internal fun logToAll(vararg log: MLog) = fun(info: TestInfo) = log.forEach { it(info) }

internal fun logToList(list: MutableList<TestInfo>) = fun(info: TestInfo) { list.add(info) }

internal fun logToConsole(info: TestInfo) = info.run {
    when (state) {
        STARTED -> println(name)
        SUCCESS -> println("SUCCESS.($location)\n")
        FAILURE -> {
            println("FAILURE.($location)")
            println("BECAUSE.($failureLocation)")
            println("$failureCause\n")
        }
        null -> println(info.toString()) // unknown state; just print everything we know
    }
}


internal class logToTree(private val tree: TestTree) : MLog {

    private var currentSubTree = tree

    override fun invoke(info: TestInfo) {

        val current = currentSubTree

        if (current === tree && tree.info.state === null) {
            check(info.state == STARTED)
            tree.reset(info)
        }
        else when (info.state) {

            STARTED -> {
                val subTree = current.subtrees.find { it.info.location == info.location }
                if (subTree !== null)
                    currentSubTree = subTree
                else {
                    currentSubTree = TestTree(info.copy())
                    current.subtrees.add(currentSubTree)
                }
            }

            SUCCESS, FAILURE -> {
                check(info.location == current.info.location)
                current.info.applyExistentFrom(info)
                currentSubTree = tree // start again from the top
            }

            null -> throw IllegalStateException("Unknown test state")
        }
    }
}