package me.qbosst.jda.ext.commands

import me.qbosst.jda.ext.commands.annotations.CommandFunction
import me.qbosst.jda.ext.commands.annotations.Greedy
import me.qbosst.jda.ext.commands.annotations.Tentative
import me.qbosst.jda.ext.commands.argument.Argument
import net.dv8tion.jda.api.Permission
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

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
                .map { param ->
                    val name = param.name ?: param.index.toString()
                    val isOptional = param.isOptional
                    val isNullable = param.type.isMarkedNullable
                    val isTentative = param.hasAnnotation<Tentative>()

                    if (isTentative && !(isNullable || isOptional))
                        throw IllegalStateException("$name is marked as tentative, but does not have a default value and is not marked nullable!")

                    Argument(
                        name = name, type = param.type.jvmErasure.javaObjectType, greedy = param.hasAnnotation<Greedy>(),
                        optional = isOptional, nullable = isNullable, isTentative = isTentative, index = param.index,
                        kParameter = param
                    )
                }

            val properties = func.findAnnotation<CommandFunction>()!!

            CommandExecutable(func, this, arguments, contextParameter, properties)
        }
        .sortedBy { executable -> executable.properties.order }

    val usages: List<String> get() = methods.map { method -> method.arguments.joinToString(" ") { it.format(true) } }
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