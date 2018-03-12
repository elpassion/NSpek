package com.elpassion.nspek

import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier

class JunitNotifierWrapper(private val notifier: RunNotifier) : (Notification) -> Unit {
    override fun invoke(notification: Notification) {
        when (notification) {
            is Notification.Start -> notifier.fireTestStarted(notification.description)
            is Notification.Failure -> {
                notifier.fireTestFailure(Failure(notification.description, notification.cause))
                notifier.fireTestFinished(notification.description)
            }
            is Notification.End -> notifier.fireTestFinished(notification.description)
        }
    }
}