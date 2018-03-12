package com.elpassion.nspek

class LoggingNotifier : (Notification) -> Unit {
    override fun invoke(it: Notification) {
        when (it) {
            is Notification.Start -> println(it.description.displayName)
            is Notification.End -> println("SUCCESS.(${it.location})\n")
            is Notification.Failure -> {
                println("FAILURE.(${it.location})")
                println("BECAUSE.(${it.cause.causeLocation})")
                println("${it.cause}\n")
            }
        }
    }
}