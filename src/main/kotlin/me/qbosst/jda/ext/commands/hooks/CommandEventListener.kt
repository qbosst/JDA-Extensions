package me.qbosst.jda.ext.commands.hooks

import me.qbosst.jda.ext.commands.Command
import me.qbosst.jda.ext.commands.CommandContext
import me.qbosst.jda.ext.commands.CommandExecutable
import me.qbosst.jda.ext.commands.entities.CheckType
import me.qbosst.jda.ext.commands.exceptions.BadArgument
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface CommandEventListener
{
    fun onBadArgument(ctx: CommandContext, executable: CommandExecutable, error: BadArgument) { throw error }

    fun onParseError(ctx: CommandContext, executable: CommandExecutable, error: Throwable) { throw error }

    fun onMultipleBadArguments(ctx: CommandContext, errors: Map<CommandExecutable, BadArgument>) {}

    fun onMultipleParsingErrors(ctx: CommandContext, errors: Map<CommandExecutable, Throwable>) {}

    fun onInternalError(error: Throwable) { throw error }

    fun onUnknownCommand(event: MessageReceivedEvent, label: String, args: List<String>) {}

    fun onNonCommandEvent(event: MessageReceivedEvent) {}

    fun onCommandPostInvoke(ctx: CommandContext, executable: CommandExecutable, failed: Boolean) {}

    fun onCommandError(ctx: CommandContext, executable: CommandExecutable, error: Throwable) { throw error }

    fun onUserMissingPermissions(ctx: CommandContext, command: Command, permissions: List<Permission>) {}

    fun onBotMissingPermissions(ctx: CommandContext, command: Command, permissions: List<Permission>) {}

    fun onFailedCheck(ctx: CommandContext, command: Command, check: CheckType) {}
}