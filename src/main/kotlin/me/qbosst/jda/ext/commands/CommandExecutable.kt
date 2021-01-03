package me.qbosst.jda.ext.commands

import me.qbosst.jda.ext.commands.annotations.CommandFunction
import me.qbosst.jda.ext.commands.argument.Argument
import me.qbosst.jda.ext.commands.entities.Executable
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class CommandExecutable(method: KFunction<*>,
                        instance: Command,
                        arguments: List<Argument>,
                        contextParameter: KParameter,
                        val properties: CommandFunction): Executable(method, instance, arguments, contextParameter)