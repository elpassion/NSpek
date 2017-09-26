package com.elpassion.mspek.data

import com.elpassion.mspek.MiniSpek
import com.elpassion.mspek.MiniSpek.o
import org.junit.Assert

fun mspek_test_8() {
    MiniSpek.mspek("some test") {
        "first test" o {
            Assert.assertTrue(false)
        }

        "second test" o {
            Assert.assertTrue(false)
        }
    }
}