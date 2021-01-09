package me.qbosst.jda.ext.commands.impl

import dev.minn.jda.ktx.CoroutineEventListener
import me.qbosst.jda.ext.commands.annotations.CommandFunction
import me.qbosst.jda.ext.commands.argument.ArgumentParser
import me.qbosst.jda.ext.commands.entities.*
import me.qbosst.jda.ext.commands.exceptions.BadArgument
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.LoggerFactory

class DefaultCommandClient(
    override val prefixProvider: PrefixProvider,
    override val listeners: Collection<CommandEventListener>,
    developerIds: Collection<Long>
): ICommandClient, CoroutineEventListener
{
    private val _commands: MutableMap<String, Command> = mutableMapOf()
    private val commandsAlias: MutableMap<String, Command> = mutableMapOf()
    override val commands: Map<String, Command> get() = _commands

    private val _developerIds: MutableCollection<Long> = developerIds.toMutableList()
    override val developerIds: Collection<Long> get() = _developerIds

    override suspend fun onEvent(event: GenericEvent)
    {
        when(event)
        {
            is MessageReceivedEvent -> onMessageReceivedEvent(event)
            is ReadyEvent -> onReadyEvent(event)
        }
    }

    private suspend fun onMessageReceivedEvent(event: MessageReceivedEvent)
    {
        // do not allow bots to use commands
        if(event.author.isBot || event.isWebhookMessage)
            return dispatch { onNonCommandEvent(event) }

        val message = event.message
        val prefixes = prefixProvider.provide(message)
        val content = message.contentRaw

        // gets the prefix used and makes sure that message is more than just the prefix
        val prefix = prefixes.firstOrNull { prefix -> content.startsWith(prefix) && content.length > prefix.length }
            ?: return dispatch { onNonCommandEvent(event) }

        val args = content.substring(prefix.length).split("\\s+".toRegex()).toMutableList()
        val label = args.removeAt(0).toLowerCase()
        val command = kotlin.run {
            val parent = this[label] ?: return dispatch { onUnknownCommand(event, label, args) }

            // get sub command
            var subCommand: Command = parent
            while(parent.children.isEmpty() && args.isNotEmpty())
            {
                val child = subCommand[args.firstOrNull() ?: break] ?: break
                args.removeAt(0)
                subCommand = child
            }

            return@run subCommand
        }

        val ctx = Context(this, event, command)

        if(command.developerOnly && !_developerIds.contains(ctx.author.idLong))
        {
            return dispatch { onCommandDeveloperOnly(ctx, command) }
        }

        if(message.isFromGuild)
        {
            if(command.userPermissions.isNotEmpty())
            {
                val member = event.member!!
                val missingPermissions = command.userPermissions
                    .filterNot { permission -> member.hasPermission(event.textChannel, permission) }

                if(missingPermissions.isNotEmpty())
                {
                    return dispatch { onUserMissingPermissions(ctx, command, missingPermissions) }
                }
            }

            if(command.botPermissions.isNotEmpty())
            {
                val member = event.guild.selfMember
                val missingPermissions = command.botPermissions
                    .filterNot { permission -> member.hasPermission(event.textChannel, permission) }

                if(missingPermissions.isNotEmpty())
                {
                    return dispatch { onBotMissingPermissions(ctx, command, missingPermissions) }
                }
            }
        }
        else if(command.guildOnly)
        {
            return dispatch { onCommandGuildOnly(ctx, command) }
        }

        // get the command method and parsed arguments

        val (method, arguments) = kotlin.runCatching {
            when {
                command.methods.size == 1 -> {
                    val method = command.methods[0]

                    kotlin.runCatching { Pair(method, ArgumentParser
                        .parseArguments(method, ctx, args, method.properties.delimiter)) }
                        .getOrElse { error ->
                            return when(error) {
                                is BadArgument -> dispatch { onBadArgument(ctx, method, error) }
                                else -> dispatch { onParseError(ctx, method, error) }
                            }
                        }
                }
                command.methods.isNotEmpty() -> {
                    val errors = mutableMapOf<CommandExecutable, Throwable>()

                    // try to parse arguments
                    command.methods.forEach { method ->
                        kotlin.runCatching { Pair(method, ArgumentParser
                            .parseArguments(method, ctx, args, method.properties.delimiter)) }
                            .onFailure { error -> errors[method] = error }
                            .onSuccess { success -> return@runCatching success }
                    }

                    when {
                        errors.any { error -> error.value !is BadArgument } ->
                            return dispatch { onMultipleParsingErrors(ctx, errors) }
                        else ->
                        {
                            @Suppress("UNCHECKED_CAST")
                            val badArguments = (errors as Map<CommandExecutable, BadArgument>)
                                .let { badArguments ->
                                    val highest = badArguments.maxOf { (_, error) -> error.expected.index }
                                    badArguments.filter { (_, error) -> error.expected.index == highest }
                                }

                            return if(badArguments.size == 1)
                            {
                                val (method, error) = badArguments.entries.first()
                                dispatch { onBadArgument(ctx, method, error) }
                            }
                            else
                            {
                                dispatch { onMultipleBadArguments(ctx, badArguments) }
                            }
                        }
                    }
                }
                else ->
                    throw IllegalArgumentException("This command does not have any methods annotated with ${CommandFunction::class.java.simpleName}!")
            }
        }.getOrElse { error -> return dispatch { onInternalError(error) } }

        // execute method
        method.execute(ctx, arguments) { success, error ->
            if(error != null)
                dispatch { onCommandError(ctx, method, error) }
            dispatch { onCommandPostInvoke(ctx, method, !success) }
        }
    }

    private fun onReadyEvent(event: ReadyEvent)
    {
        if(_developerIds.isEmpty())
        {
            event.jda.retrieveApplicationInfo().queue { info -> _developerIds.add(info.owner.idLong) }
        }
    }

    operator fun contains(label: String) = label.toLowerCase().let { labelLower ->
        _commands.containsKey(labelLower) || commandsAlias.containsKey(labelLower)
    }

    operator fun get(label: String): Command? = label.toLowerCase().let { labelLower ->
        _commands[labelLower] ?: commandsAlias[labelLower]
    }

    fun put(command: Command) = also {
        _commands[command.label.toLowerCase()] = command
        command.aliases.forEach { alias -> commandsAlias[alias.toLowerCase()] = command }
    }

    fun put(collection: Collection<Command>) = also { collection.forEach { command -> put(command) } }


    private suspend fun dispatch(invoker: suspend CommandEventListener.() -> Unit)
    {
        kotlin.runCatching { listeners.forEach { listener -> invoker.invoke(listener) } }
            .onFailure { t ->
                kotlin.runCatching { listeners.forEach { listener -> listener.onInternalError(t) } }
                    .onFailure { inner -> LOG.error("An uncaught exception occurred during event dispatch!", inner) }
            }
    }

    companion object
    {
        private val LOG = LoggerFactory.getLogger(DefaultCommandClient::class.java)
    }
}