package me.qbosst.jda.ext.async

import dev.minn.jda.ktx.CoroutineEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.internal.JDAImpl
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Copy of [dev.minn.jda.ktx.CoroutineEventManager] but it surrounds every listener with a try block so if
 * any exceptions are thrown the other event listeners can still execute
 *
 * This supports both [EventListener] and [CoroutineEventListener]
 */
class HandledCoroutineEventManager(private val scope: CoroutineScope = GlobalScope): IEventManager {
    private val listeners = CopyOnWriteArrayList<Any>()

    override fun handle(event: GenericEvent) {
        scope.launch {
            listeners.forEach { listener ->
                try {
                    when(listener) {
                        is CoroutineEventListener -> listener.onEvent(event)
                        is EventListener -> listener.onEvent(event)
                    }
                }
                catch (t: Throwable) {
                    JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", t)
                    if (t is Error)
                        throw t
                }
            }
        }
    }

    override fun getRegisteredListeners(): MutableList<Any> = mutableListOf(listeners)

    override fun register(listener: Any) {
        listeners.add(when(listener) {
            is EventListener, is CoroutineEventListener -> listener
            else -> throw IllegalArgumentException("Listener must implement either EventListener or CoroutineEventListener")
        })
    }

    override fun unregister(listener: Any) {
        listeners.remove(listener)
    }
}