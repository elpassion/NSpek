package com.elpassion.nspek

import org.junit.runner.Description

sealed class Notification {
    abstract val description: Description
    abstract val location: CodeLocation

    data class Start(override val description: Description, override val location: CodeLocation) : Notification()
    data class End(override val description: Description, override val location: CodeLocation) : Notification()
    data class Failure(override val description: Description, override val location: CodeLocation, val cause: Throwable) : Notification()
}