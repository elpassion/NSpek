package com.elpassion.mspek.data

import com.elpassion.mspek.MiniSpek
import com.elpassion.mspek.MiniSpek.o
import org.junit.Assert

fun mspek_test_2() {
    MiniSpek.mspek("some nested test") {
        "some assertion" o {
            Assert.assertTrue(true)
        }
    }
}