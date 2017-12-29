package com.elpassion.nspek

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith

@RunWith(NSpekRunner::class)
class NSpekRunnerExample {

    @Test
    fun NSpekMethodContext.test() {
        try {
            "subtest" o {
                assertTrue(true)
                extractedAssertion()
            }
        } finally {
            
        }
        assertTrue(true)
    }

    private fun NSpekMethodContext.extractedAssertion() =
            "nested-subtest" o {
                assertFalse(false)
            }
}