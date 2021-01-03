package me.qbosst.jda.ext.commands

import dev.minn.jda.ktx.CoroutineEventListener
import me.qbosst.jda.ext.commands.argument.ArgumentParser
import me.qbosst.jda.ext.commands.entities.CheckType
import me.qbosst.jda.ext.commands.entities.PrefixProvider
import me.qbosst.jda.ext.commands.exceptions.BadArgument
import me.qbosst.jda.ext.commands.hooks.CommandEventListener
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.LoggerFactory

class CommandClient(private val prefixProvider: PrefixProvider,
                    private val listeners: List<CommandEventListener>,
                    private val ignoreBots: Boolean,
                    private val developerIds: MutableCollection<Long>
): CoroutineEventListener
{
    private val _commands = mutableMapOf<String, Command>()
    private val commandsAlias = mutableMapOf<String, Command>()

    val commands: Map<String, Command> get() = _commands

    private suspend fun onMessageReceivedEvent(event: MessageReceivedEvent)
    {
        if(ignoreBots && (event.author.isBot || event.isWebhookMessage))
            return dispatch { onNonCommandEvent(event) }

        val message = event.message
        val prefixes = prefixProvider.provide(message)

        // checks if message starts with prefix and message length is higher than prefix
        val content = message.contentRaw
        val prefix = prefixes.firstOrNull { prefix -> content.startsWith(prefix) && prefix.length < content.length }
            ?: return dispatch { onNonCommandEvent(event) }

        val args = content.substring(prefix.length).split("\\s+".toRegex()).toMutableList()
        val label = args.removeAt(0).toLowerCase()

        val command = this[label]
            ?.let { parent ->
                var command = parent
                while (command.children.isNotEmpty())
                {
                    val arg = args.firstOrNull() ?: break
                    val child = command[arg] ?: break
                    args.removeAt(0)
                    command = child
                }
                return@let command
            }
            ?: return dispatch { onUnknownCommand(event, label, args) }

        val ctx = CommandContext(this, event, prefix, command)

        if(command.developerOnly && !developerIds.contains(event.author.idLong))
            return dispatch { onFailedCheck(ctx, command, CheckType.DEVELOPER_ONLY) }

        if(message.isFromGuild)
        {
            if(command.userPermissions.isNotEmpty())
            {
                val member = event.member!!
                val missingPermissions = command.userPermissions
                    .filterNot { permission -> member.hasPermission(event.textChannel, permission) }

                if(missingPermissions.isNotEmpty())
                    return dispatch { onUserMissingPermissions(ctx, command, missingPermissions) }
            }

            if(command.botPermissions.isNotEmpty())
            {
                val missingPermissions = command.botPermissions
                    .filterNot { permission -> event.guild.selfMember.hasPermission(event.textChannel, permission) }

                if(missingPermissions.isNotEmpty())
                    return dispatch { onBotMissingPermissions(ctx, command, missingPermissions) }
            }
        }
        else if(command.guildOnly)
        {
            return dispatch { onFailedCheck(ctx, command, CheckType.GUILD_ONLY) }
        }


        val (method, arguments) = kotlin.runCatching()
        {
            when
            {
                command.methods.size == 1 ->
                {
                    val method = command.methods[0]

                    kotlin.runCatching { Pair(method, ArgumentParser
                        .parseArguments(method, ctx, args, method.properties.delimiter)) }
                        .getOrElse { error ->
                            return when(error)
                            {
                                is BadArgument -> dispatch { onBadArgument(ctx, method, error) }
                                else -> dispatch { onParseError(ctx, method, error) }
                            }
                        }
                }

                command.methods.isNotEmpty() ->
                {
                    val errors = mutableMapOf<CommandExecutable, Throwable>()
                    for(method in command.methods)
                        // try to parse arguments
                        kotlin.runCatching { Pair(method, ArgumentParser
                            .parseArguments(method, ctx, args, method.properties.delimiter)) }
                            .onFailure { error -> errors[method] = error }
                            .onSuccess { success -> return@runCatching success }

                    when
                    {
                        errors.any { error -> error.value !is BadArgument } ->
                            return dispatch { onMultipleParsingErrors(ctx, errors) }

                        else ->
                        {
                            @Suppress("UNCHECKED_CAST")
                            val badArguments = (errors as Map<CommandExecutable, BadArgument>)
                                .let { badArguments ->
                                    val highest = badArguments.values.maxOf { arg -> arg.expected.index }
                                    badArguments.filter { error -> error.value.expected.index == highest }
                                }

                            return if(badArguments.size == 1)
                            {
                                badArguments.entries.first()
                                    .let { (method, error) -> dispatch { onBadArgument(ctx, method, error) } }
                            }
                            else
                            {
                                dispatch { onMultipleBadArguments(ctx, errors) }
                            }
                        }
                    }
                }
                else ->
                    throw UnsupportedOperationException()
            }
        }
            .getOrElse { error -> return dispatch { onInternalError(error) } }

        // execute method
        method.execute(ctx, arguments) { success, error ->

            if(error != null)
                dispatch { onCommandError(ctx, method, error) }

            dispatch { onCommandPostInvoke(ctx, method, !success) }
        }
    }

    private fun onReadyEvent(event: ReadyEvent)
    {
        if(developerIds.isEmpty())
            event.jda.retrieveApplicationInfo().queue { info -> developerIds.add(info.owner.idLong) }
    }

    operator fun get(label: String) = label.toLowerCase().let { _commands[label] ?: commandsAlias[label] }

    fun put(command: Command) = apply {
        _commands[command.label.toLowerCase()] = command
        command.aliases.forEach { alias -> commandsAlias[alias.toLowerCase()] = command }
    }

    fun put(commands: Collection<Command>) = apply { commands.forEach { command -> put(command) } }

    override suspend fun onEvent(event: GenericEvent)
    {
        try
        {
            when(event)
            {
                is MessageReceivedEvent -> onMessageReceivedEvent(event)
                is ReadyEvent -> onReadyEvent(event)
            }
        }
        catch(e: Throwable)
        {
            dispatch { onInternalError(e) }
        }
    }

    private suspend fun dispatch(invoker: suspend CommandEventListener.() -> Unit)
    {
        try
        {
            for(listener in listeners)
                invoker.invoke(listener)
        }
        catch (t: Throwable)
        {
            try
            {
                for(listener in listeners)
                    invoker.invoke(listener)
            }
            catch (inner: Throwable)
            {
                LOG.error("An uncaught exception occurred during event dispatch!", inner)
            }
        }
    }

    companion object
    {
        private val LOG = LoggerFactory.getLogger(CommandClient::class.java)
    }
}