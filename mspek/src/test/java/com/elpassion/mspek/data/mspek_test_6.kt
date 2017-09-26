package com.elpassion.mspek.data

import com.elpassion.mspek.MiniSpek
import com.elpassion.mspek.MiniSpek.o

fun mspek_test_6() {
    MiniSpek.mspek("some test") {
        "first test" o {
        }

        "second test" o {
        }
    }
}