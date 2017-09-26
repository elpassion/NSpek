package com.elpassion.mspek.data

import com.elpassion.mspek.MiniSpek
import com.elpassion.mspek.MiniSpek.o
import org.junit.Assert

fun mspek_test_4() {
    MiniSpek.mspek("some test") {
        "some assertion" o {
            Assert.assertTrue(false)
        }
    }
}