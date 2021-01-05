package me.qbosst.jda.ext.commands.entities

import me.qbosst.jda.ext.commands.exceptions.BadArgument
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface CommandEventListener
{
    /**
     * Invoked when a [BadArgument] was thrown. This indicates that a user gave a bad argument.
     * For example, giving a [String] instead of an [Int]
     */
    fun onBadArgument(ctx: Context, executable: CommandExecutable, error: BadArgument) { throw error }

    /**
     * Invoked when an exception other than [BadArgument] was thrown during parsing the message arguments.
     * A common cause of the [error] is [me.qbosst.jda.ext.commands.exceptions.ParserNotRegistered].
     */
    fun onParseError(ctx: Context, executable: CommandExecutable, error: Throwable) { throw error }

    /**
     * Invoked when a [Command] has multiple [Command.methods] and a bad argument was given.
     * For example, giving a [String] instead of an [Int] or [Double].
     */
    fun onMultipleBadArguments(ctx: Context, errors: Map<CommandExecutable, BadArgument>) {}

    fun onMultipleParsingErrors(ctx: Context, errors: Map<CommandExecutable, Throwable>) {}

    fun onInternalError(error: Throwable) { throw error }

    /**
     * Invoked when a [MessageReceivedEvent] was received and started with a [Command] prefix,
     * but the [label] did not match any [Command] labels
     */
    fun onUnknownCommand(event: MessageReceivedEvent, label: String, args: List<String>) {}

    /**
     * Invoked when a [MessageReceivedEvent] was received but did not start with a [Command] prefix.
     */
    fun onNonCommandEvent(event: MessageReceivedEvent) {}

    /**
     * Invoked when a [Command] has finished executing regardless of an exception
     */
    fun onCommandPostInvoke(ctx: Context, executable: CommandExecutable, failed: Boolean) {}

    /**
     * Invoked when a command has thrown an [error] during execution.
     */
    fun onCommandError(ctx: Context, executable: CommandExecutable, error: Throwable) { throw error }

    /**
     * Invoked when a [net.dv8tion.jda.api.entities.Member] does not have the required [permissions] for a command.
     */
    fun onUserMissingPermissions(ctx: Context, command: Command, permissions: List<Permission>) {}

    /**
     * Invoked when the bot does not have the required [permissions] for a command.
     */
    fun onBotMissingPermissions(ctx: Context, command: Command, permissions: List<Permission>) {}

    /**
     * Invoked when a developer-only command has been invoked from someone who is not a developer.
     */
    fun onCommandDeveloperOnly(ctx: Context, command: Command) {}

    /**
     * Invoked when a [net.dv8tion.jda.api.entities.Guild]-only command has been invoked from outside of a guild.
     */
    fun onCommandGuildOnly(ctx: Context, command: Command) {}
}