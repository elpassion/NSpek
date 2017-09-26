package com.elpassion.mspek

import com.elpassion.mspek.MiniSpek.o
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MiniSpekRunnerTest {

    @Test
    fun `should create instance of test class`() {
        var created = false
        TestClass.code = { created = true }
        MiniSpekRunner(TestClass::class.java)
        assertTrue(created)
    }

    @Test
    fun `should create description with a class name for an empty test suite`() {
        val runner = MiniSpekRunner(TestClass::class.java)
        assertEquals("TestClass", runner.description.className)
    }

    @Test
    fun `should mspek description with a name of test`() {
        TestClass.code = {
            MiniSpek.mspek("some test") { }
        }
        val runner = MiniSpekRunner(TestClass::class.java)
        val testCase = runner.description.children.first()
        assertEquals("some test(com.elpassion.mspek.TestClass)", testCase.displayName)
    }

    @Test
    fun `should create test case under test suite`() {
        TestClass.code = {
            MiniSpek.mspek("some nested test") {
                "some assertion" o { }
            }
        }
        val runner = MiniSpekRunner(TestClass::class.java)
        val testCase = runner.description.children.first().children.first()
        assertEquals("some assertion(com.elpassion.mspek.TestClass.some nested test)", testCase.displayName)
        assertTrue(testCase.isTest)
        assertEquals(1, runner.testCount())
    }

    @Test
    fun `should create two test cases under test suite`() {
        TestClass.code = {
            MiniSpek.mspek("some test") {
                "first test" o { }
                "second test" o { }
            }
        }
        val runner = MiniSpekRunner(TestClass::class.java)
        val testSuite = runner.description.children.first().children
        val first = testSuite.first()
        val second = testSuite[1]
        assertEquals("first test(com.elpassion.mspek.TestClass.some test)", first.displayName)
        assertTrue(first.isTest)
        assertEquals("second test(com.elpassion.mspek.TestClass.some test)", second.displayName)
        assertTrue(second.isTest)
        assertEquals(2, runner.testCount())
    }

    @Test
    fun `should handle two levels of nesting`() {
        TestClass.code = {
            MiniSpek.mspek("some test") {
                "first test" o {
                    "second test" o { }
                }
            }
        }
        val runner = MiniSpekRunner(TestClass::class.java)
        val testSuite = runner.description.children.first().children
        val first = testSuite.first()
        val second = first.children.first()
        assertEquals("first test", first.displayName)
        assertTrue(first.isSuite)
        assertEquals("second test(com.elpassion.mspek.TestClass.some test.first test)", second.displayName)
        assertTrue(second.isTest)
        assertEquals(1, runner.testCount())
    }

    @Test
    fun `should handle mixed levels of nesting`() {
        TestClass.code = {
            MiniSpek.mspek("some test") {
                "first test" o {
                    "second test" o { }
                }
                "third test" o { }
            }
        }
        val runner = MiniSpekRunner(TestClass::class.java)
        val testSuite = runner.description.children.first().children
        val first = testSuite.first()
        val second = first.children.first()
        val third = testSuite[1]
        assertEquals("first test", first.displayName)
        assertTrue(first.isSuite)
        assertEquals("second test(com.elpassion.mspek.TestClass.some test.first test)", second.displayName)
        assertTrue(second.isTest)
        assertEquals("third test(com.elpassion.mspek.TestClass.some test)", third.displayName)
        assertTrue(third.isTest)
        assertEquals(2, runner.testCount())
    }
}

private class TestClass {
    init {
        code?.invoke()
    }

    companion object {
        var code: (() -> Unit)? = null
    }
}