package com.elpassion.nspek

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class NSpekRunner(testClass: Class<*>) : Runner() {
    private val notifications: List<Notification>
    private val rootDescription: Description

    init {
        val testSelector = createTestSelector()
        val (rootDescription, runResult) = runClassTests(testClass, testSelector)
        this.rootDescription = rootDescription
        this.notifications = runResult
    }

    private fun createTestSelector(): TestSelector {
        val property = System.getProperty("testPath")
        return if (property == null) {
            ALL_TEST_ALLOWED_SELECTOR
        } else {
            createRestrictingTestSelector(property)
        }
    }

    private fun createRestrictingTestSelector(property: String): (String) -> Boolean {
        return { testPath ->
            val allowedPath = property.split("|")
            allowedPath.containsAll(testPath.split("|").take(allowedPath.size))
        }
    }

    override fun getDescription(): Description = rootDescription

    override fun run(notifier: RunNotifier) {
        val notifiers = customNotifiers + JunitNotifierWrapper(notifier)
        notifications.forEach { notification ->
            notifiers.forEach { notifier ->
                notifier.invoke(notification)
            }
        }
    }

    companion object {
        var customNotifiers = listOf<(Notification) -> Unit>(LoggingNotifier())
    }
}

fun runClassTests(testClass: Class<*>, testSelector: (String) -> Boolean): Pair<Description, List<Notification>> {
    require(testClass.methods.filter { it.getAnnotation(Test::class.java) != null }.isNotEmpty()) { "At least one method should be annotated with com.elpassion.nspek.Test" }
    val descriptions = runMethodsTests(testClass, testSelector).map { testBranch ->
        testBranch.copy(names = listOf(testClass.name) + testBranch.names)
    }
    val descriptionTree = descriptions.toTree()
    return descriptionTree.getDescriptions().first() to descriptionTree.getNotifications()
}

private fun runMethodsTests(testClass: Class<*>, testSelector: (String) -> Boolean): List<TestBranch> {
    return testClass.declaredMethods.filter { it.getAnnotation(Test::class.java) != null }.flatMap { method ->
        try {
            val results = runMethodTests(method, testClass, testSelector).map { testBranch ->
                testBranch.copy(names = listOf(method.name) + testBranch.names)
            }
            if (results.isNotEmpty()) {
                results
            } else {
                listOf(TestBranch(names = listOf(method.name), location = currentUserCodeLocation))
            }
        } catch (t: Throwable) {
            listOf(TestBranch(names = listOf(method.name), throwable = t, location = currentUserCodeLocation))
        }
    }
}

private fun runMethodTests(method: Method, testClass: Class<*>, testSelector: (String) -> Boolean): List<TestBranch> {
    val descriptionsNames = mutableListOf<TestBranch>()
    val finishedTestNames = mutableListOf<String>()
    while (true) {
        val nSpekContext = NSpekMethodContext(finishedTestNames, testSelector)
        try {
            method.invoke(testClass.newInstance(), nSpekContext)
            break
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            if (cause is TestEnd) {
                finishedTestNames.add(createTestPath(nSpekContext.names))
                descriptionsNames.add(TestBranch(nSpekContext.names, cause.cause, location = cause.codeLocation))
            } else {
                throw cause!!
            }
        }
    }
    return descriptionsNames
}

private data class TestBranch(val names: List<String>, val throwable: Throwable? = null, val location: CodeLocation)

private sealed class InfiniteMap : MutableMap<String, InfiniteMap> by mutableMapOf() {
    data class Branch(val throwable: Throwable? = null, val description: Description, val location: CodeLocation) : InfiniteMap()
    class Root : InfiniteMap()
}

private fun List<TestBranch>.toTree(): InfiniteMap {
    val map: InfiniteMap = InfiniteMap.Root()
    forEach { (names, throwable, location) ->
        names.foldIndexed(map, { index, acc, name ->
            acc.getOrPut(name, {
                val description = if (index != names.lastIndex) {
                    Description.createSuiteDescription(name)
                } else {
                    Description.createTestDescription(names[index - 1], name)
                }
                InfiniteMap.Branch(description = description, throwable = throwable, location = location)
            })
        })
    }
    return map
}

private fun InfiniteMap.getDescriptions(): List<Description> {
    return values.filterIsInstance<InfiniteMap.Branch>().map { map ->
        if (map.isNotEmpty()) {
            map.description.addAllChildren(map.getDescriptions())
        } else {
            map.description
        }
    }
}

private fun InfiniteMap.getNotifications(): List<Notification> {
    return if (this is InfiniteMap.Branch && isEmpty()) {
        val startNotification = listOf(Notification.Start(description, location))
        val endNotification = if (throwable != null) {
            Notification.Failure(description, location, throwable)
        } else {
            Notification.End(description, location)
        }
        startNotification + values.flatMap { it.getNotifications() } + endNotification
    } else {
        values.flatMap { it.getNotifications() }
    }
}

private fun Description.addAllChildren(descriptions: List<Description>) = apply {
    descriptions.forEach {
        addChild(it)
    }
}

typealias TestSelector = (String) -> Boolean

val ALL_TEST_ALLOWED_SELECTOR: TestSelector = { true }