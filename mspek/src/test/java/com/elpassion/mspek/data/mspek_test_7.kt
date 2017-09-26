package com.elpassion.mspek.data

import com.elpassion.mspek.MiniSpek
import com.elpassion.mspek.MiniSpek.o
import org.junit.Assert

fun mspek_test_7() {
    MiniSpek.mspek("some test") {
        "first test" o {
            Assert.assertTrue(true)
        }

        "second test" o {
            Assert.assertTrue(true)
        }
    }
}