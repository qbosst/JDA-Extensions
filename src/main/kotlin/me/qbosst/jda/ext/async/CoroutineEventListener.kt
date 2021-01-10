package me.qbosst.jda.ext.async

import net.dv8tion.jda.api.events.GenericEvent

interface CoroutineEventListener
{
    suspend fun onEvent(event: GenericEvent)
}