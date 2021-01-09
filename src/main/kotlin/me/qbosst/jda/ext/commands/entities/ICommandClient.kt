package me.qbosst.jda.ext.commands.entities

interface ICommandClient
{
    val commands: Map<String, Command>
    val listeners: Collection<CommandEventListener>
    val developerIds: Collection<Long>
    val prefixProvider: PrefixProvider
}