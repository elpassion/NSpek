package com.elpassion.nspek

import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Test

private val codeFragment = mock<NSpekCodeInvocationTest.CodeFragment>()

class NSpekTestSelectorTest {

    @Test
    fun shouldInvokeCodeWitchIsInside() {
        runClassTests(ExampleTestClass::class.java, ALL_TEST_ALLOWED_SELECTOR)
        verify(codeFragment, atLeast(1)).first()
        verify(codeFragment, atLeast(1)).second()
        verify(codeFragment, atLeast(1)).third()
        verify(codeFragment, atLeast(1)).fourth()
        verify(codeFragment, atLeast(1)).fifth()
        verify(codeFragment, atLeast(1)).sixth()
    }

    @Test
    fun shouldInvokeOnlyBeforeAndAfter() {
        val testSelector: TestSelector = { testPath -> false }
        runClassTests(ExampleTestClass::class.java, testSelector)
        verify(codeFragment, atLeast(1)).first()
        verify(codeFragment, never()).second()
        verify(codeFragment, never()).third()
        verify(codeFragment, never()).fourth()
        verify(codeFragment, never()).fifth()
        verify(codeFragment, atLeast(1)).sixth()
    }

    @Test
    fun shouldInvokeCodeOnlyInAllowedBranch() {
        val testSelector: TestSelector = { testPath -> testPath == "sub-test" }
        runClassTests(ExampleTestClass::class.java, testSelector)
        verify(codeFragment, atLeast(1)).first()
        verify(codeFragment, atLeast(1)).second()
        verify(codeFragment, never()).third()
        verify(codeFragment, never()).fourth()
        verify(codeFragment, never()).fifth()
        verify(codeFragment, atLeast(1)).sixth()
    }

    @Test
    fun shouldNotInvokeCodeInChildBranches() {
        val testSelector: TestSelector = { testPath -> testPath == "sub-suite" }
        runClassTests(ExampleTestClass::class.java, testSelector)
        verify(codeFragment, atLeast(1)).first()
        verify(codeFragment, never()).second()
        verify(codeFragment, atLeast(1)).third()
        verify(codeFragment, never()).fourth()
        verify(codeFragment, never()).fifth()
        verify(codeFragment, atLeast(1)).sixth()
    }

    class ExampleTestClass {
        @com.elpassion.nspek.Test
        fun NSpekMethodContext.test() {
            codeFragment.first()
            "sub-test" o {
                codeFragment.second()
            }
            "sub-suite" o {
                codeFragment.third()
                "nested-subtest" o {
                    codeFragment.fourth()
                }
                "second-nested-subtest" o {
                    codeFragment.fifth()
                }
            }
            codeFragment.sixth()
        }
    }

    @After
    fun tearDown() {
        reset(codeFragment)
    }

    interface CodeFragment {
        fun first()
        fun second()
        fun third()
        fun fourth()
        fun fifth()
        fun sixth()
    }
}