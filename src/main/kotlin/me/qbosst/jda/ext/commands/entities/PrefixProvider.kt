package me.qbosst.jda.ext.commands.entities

import net.dv8tion.jda.api.entities.Message

interface PrefixProvider
{
    fun provide(message: Message): Collection<String>
}