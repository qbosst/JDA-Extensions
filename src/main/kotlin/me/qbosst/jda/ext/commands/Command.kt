package me.qbosst.jda.ext.commands

import me.qbosst.jda.ext.commands.annotations.CommandFunction
import me.qbosst.jda.ext.commands.argument.Argument
import net.dv8tion.jda.api.Permission
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.valueParameters

abstract class Command
{
    abstract val label: String

    open val description: String? = null
    open val aliases: Collection<String> = listOf()

    open val guildOnly: Boolean = false
    open val developerOnly: Boolean = false

    open val userPermissions: Collection<Permission> = listOf()
    open val botPermissions: Collection<Permission> = listOf()

    val methods: List<CommandExecutable> = this::class.members
        .filterIsInstance<KFunction<*>>()
        .filter { func -> func.hasAnnotation<CommandFunction>() }
        .map { func ->
            val contextParameter = func.valueParameters
                .firstOrNull { param -> param.type.classifier?.equals(CommandContext::class) == true }
            require(contextParameter != null) { "${func.name} is missing the Context parameter!" }

            val arguments = func.valueParameters
                .filterNot { param -> param.type.classifier?.equals(CommandContext::class) == true }
                .map { parameter -> Argument(parameter) }

            val properties = func.findAnnotation<CommandFunction>()!!

            CommandExecutable(func, this, arguments, contextParameter, properties)
        }
        .sortedBy { executable -> executable.properties.priority }

    val usages: List<String>
        get() = methods
            .filter { method -> method.properties.includeUsage }
            .map { method -> method.arguments.joinToString(" ") { it.format(true) } }

    val examples: List<String> get() = methods.map { method -> method.properties.examples.toList() }.flatten()

    var parent: Command? = null
        private set

    private val _children: MutableCollection<Command> = mutableListOf()
    val children: Collection<Command> get() = _children

    protected fun addChild(child: Command) = also {
        child.parent = this
        _children.add(child)
    }

    protected fun addChildren(children: Collection<Command>) = also { children.forEach { child -> addChild(child) } }

    operator fun get(label: String): Command? = children.firstOrNull { child -> child.label.equals(label, true) ||
            child.aliases.any { alias -> alias.equals(label, true) }
    }
}