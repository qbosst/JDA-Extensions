package me.qbosst.jda.ext.commands.entities

interface CommandClient
{
    val commands: Map<String, Command>
    val listeners: Collection<CommandEventListener>
    val developerIds: Collection<Long>
    val prefixProvider: PrefixProvider
}