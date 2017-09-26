package com.elpassion.mspek.data

import com.elpassion.mspek.MiniSpek
import com.elpassion.mspek.MiniSpek.o
import org.junit.Assert

fun mspek_test_5() {
    MiniSpek.mspek("some test") {
        "some nested test" o {
            Assert.assertTrue(false)
        }
    }
}