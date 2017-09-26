package com.elpassion.mspek.data

import com.elpassion.mspek.MiniSpek.o
import com.elpassion.mspek.MiniSpek.mspek
import org.junit.Assert

fun mspek_test_3() {
    mspek("some test") {
        "some assertion" o {
            Assert.assertTrue(true)
        }
    }
}