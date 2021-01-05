package me.qbosst.jda.ext.commands.entities

import me.qbosst.jda.ext.commands.annotations.CommandFunction
import me.qbosst.jda.ext.commands.argument.Argument
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ISnowflake
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.valueParameters

abstract class Command
{
    /**
     * The name of the command.
     */
    abstract val label: String

    /**
     * A small summary of what the command does.
     */
    open val description: String? = null

    /**
     * Alternative names that the [Command] can be called by.
     */
    open val aliases: Collection<String> = listOf()

    /**
     * Whether the [Command] can only be used in a [net.dv8tion.jda.api.entities.Guild] or not
     */
    open val guildOnly: Boolean = false

    /**
     * Whether the [Command] can only be used by developers
     */
    open val developerOnly: Boolean = false

    /**
     * The [Permission]s that a [net.dv8tion.jda.api.entities.User] is required to have to use this [Command].
     */
    open val userPermissions: Collection<Permission> = listOf()

    /**
     * The [Permission]s that the bot is required to have to use this [Command].
     */
    open val botPermissions: Collection<Permission> = listOf()

    val methods: List<CommandExecutable> = this::class.members
        .filterIsInstance<KFunction<*>>()
        .filter { func -> func.hasAnnotation<CommandFunction>() }
        .map { func ->
            val contextParameter = func.valueParameters
                .firstOrNull { param -> param.type.classifier?.equals(Context::class) == true }
            require(contextParameter != null) { "${func.name} is missing the Context parameter!" }

            val arguments = func.valueParameters
                .filterNot { param -> param.type.classifier?.equals(Context::class) == true }
                .map { parameter -> Argument(parameter) }

            val properties = func.findAnnotation<CommandFunction>()!!

            CommandExecutable(func, this, arguments, contextParameter, properties)
        }
        .sortedBy { executable -> executable.properties.priority }

    /**
     * A list of usages, demonstrating the arguments you need to use this [Command]
     */
    val usages: List<String>
        get() = methods
            .filter { method -> method.properties.includeUsage }
            .map { method -> method.arguments.joinToString(" ") { arg -> arg.format(formatWithType) } }

    /**
     * A list of examples, demonstrating how to use this [Command] based on the [usages]
     */
    val examples: List<String> get() = methods.map { method -> method.properties.examples.toList() }.flatten()

    /**
     * The parent of this [Command]. If [parent] is not null it means that this command is a sub-command of [parent].
     */
    var parent: Command? = null
        private set

    private val _children: MutableCollection<Command> = mutableListOf()

    /**
     * The Sub-[Command]s of this command. This is executed in the format <prefix>(label) (child.label)
     */
    val children: Collection<Command> get() = _children

    protected fun addChild(child: Command) = also {
        child.parent = this
        _children.add(child)
    }

    protected fun addChildren(children: Collection<Command>) = also { children.forEach { child -> addChild(child) } }

    operator fun get(label: String): Command? = children.firstOrNull { child -> child.label.equals(label, true) ||
            child.aliases.any { alias -> alias.equals(label, true) }
    }

    companion object
    {
        var formatWithType: Boolean = true
    }
}