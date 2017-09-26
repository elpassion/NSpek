package com.elpassion.mspek

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import com.elpassion.mspek.TestState.*
import com.elpassion.mspek.data.*

class MiniSpekTest {

    private val infos = mutableListOf<TestInfo>()

    @Before
    fun setUp() {
        MiniSpek.log = logToList(infos)
    }

    @Test
    fun `should create start report at the beginning of mspek`() {
        mspek_test_1()
        assertThat(infos).isEqualTo(listOf(TestInfo("some test", CodeLocation("mspek_test_1.kt", 6), state = STARTED)))
    }

    @Test
    fun `should create start report at the beginning of nested test`() {
        mspek_test_2()
        assertThat(infos).contains(TestInfo("some nested test", CodeLocation("mspek_test_2.kt", 8), state = STARTED))
    }

    @Test
    fun `should create success report after finishing test with success`() {
        mspek_test_3()
        assertThat(infos).contains(TestInfo(location = CodeLocation("mspek_test_3.kt", 9), state = SUCCESS))
    }

    @Test
    fun `should create failure report after finishing test with error`() {
        mspek_test_4()
        val actual = infos.filter { it.state == FAILURE }
        val expected = TestInfo(location = CodeLocation("mspek_test_4.kt", 9),
                state = FAILURE, failureLocation = CodeLocation("mspek_test_4.kt", 10),
                failureCause = actual[0].failureCause!!)
        assertThat(actual).contains(expected)
    }

    @Test
    fun `should start all outer clauses in proper order`() {
        mspek_test_5()
        assertThat(infos)
                .containsSequence(listOf(
                        TestInfo("some test", CodeLocation("mspek_test_5.kt", 8), state = STARTED),
                        TestInfo("some nested test", CodeLocation("mspek_test_5.kt", 9), state = STARTED)))
    }

    @Test
    fun `should start all nested tests`() {
        mspek_test_6()
        assertThat(infos)
                .containsAll(listOf(
                        TestInfo("first test", CodeLocation("mspek_test_6.kt", 8), state = STARTED),
                        TestInfo("second test", CodeLocation("mspek_test_6.kt", 11), state = STARTED)))
    }

    @Test
    fun `should gather success from all nested tests`() {
        mspek_test_7()
        assertThat(infos)
                .containsAll(listOf(
                        TestInfo(location = CodeLocation("mspek_test_7.kt", lineNumber = 9), state = SUCCESS),
                        TestInfo(location = CodeLocation("mspek_test_7.kt", lineNumber = 13), state = SUCCESS)))
    }

    @Test
    fun `should gather failures from all nested tests`() {
        mspek_test_8()
        val actual = infos.filter { it.state == FAILURE }
        val expected = listOf(
                TestInfo(location = CodeLocation("mspek_test_8.kt", lineNumber = 9),
                        state = FAILURE, failureLocation = CodeLocation("mspek_test_8.kt", lineNumber = 10),
                        failureCause = actual[0].failureCause!!),
                TestInfo(location = CodeLocation("mspek_test_8.kt", lineNumber = 13),
                        state = FAILURE, failureLocation = CodeLocation("mspek_test_8.kt", lineNumber = 14),
                        failureCause = actual[1].failureCause!!))
        assertThat(actual).containsSequence(expected)
    }

    @Test
    fun `should gather all failures along with successes`() {
        mspek_test_9()
        val expected = listOf(
                TestInfo("some test", CodeLocation("mspek_test_9.kt", 8), state = STARTED),
                TestInfo("first test", CodeLocation("mspek_test_9.kt", 9), state = STARTED),
                TestInfo(location = CodeLocation("mspek_test_9.kt", lineNumber = 9),
                        state = FAILURE, failureLocation = CodeLocation("mspek_test_9.kt", lineNumber = 10),
                        failureCause = infos[2].failureCause!!),
                TestInfo("second test", CodeLocation("mspek_test_9.kt", 13), state = STARTED),
                TestInfo(location = CodeLocation("mspek_test_9.kt", lineNumber = 13), state = SUCCESS))
        assertThat(infos).isEqualTo(expected)
    }

    @Test
    fun `should execute tests which are nested multiple times`() {
        mspek_test_10()
        assertThat(infos)
                .containsSequence(listOf(TestInfo("some test", CodeLocation("mspek_test_10.kt", 8), state = STARTED),
                        TestInfo("first test", CodeLocation("mspek_test_10.kt", 9), state = STARTED),
                        TestInfo("second test", CodeLocation("mspek_test_10.kt", 12), state = STARTED),
                        TestInfo(location = CodeLocation("mspek_test_10.kt", lineNumber = 12), state = SUCCESS),
                        TestInfo("first test", CodeLocation("mspek_test_10.kt", 9), state = STARTED),
                        TestInfo(location = CodeLocation("mspek_test_10.kt", lineNumber = 9), state = SUCCESS)
                ))
    }
}

