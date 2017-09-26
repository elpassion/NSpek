package com.elpassion.mspek

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import com.elpassion.mspek.MiniSpek.mspek
import com.elpassion.mspek.MiniSpek.o

@RunWith(MiniSpekRunner::class)
class MiniSpekRunnerExample {

    @Test
    fun someTest() {
        mspek("some nested test") {
            "1st assertion" o {
                Assert.assertTrue(true)
                "nested assertion" o {
                    Assert.assertTrue(true)
                    "even more nested assertion" o {
                        Assert.assertTrue(true)
                    }
                }
            }
            "2nd assertion" o {
                Assert.assertFalse(false)
            }
            "3rd assertion" o {
                Assert.assertFalse(false)
            }
        }
    }
}