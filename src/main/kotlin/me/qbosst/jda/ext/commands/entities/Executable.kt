package me.qbosst.jda.ext.commands.entities

import me.qbosst.jda.ext.commands.Command
import me.qbosst.jda.ext.commands.CommandContext
import me.qbosst.jda.ext.commands.argument.Argument
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.instanceParameter

open class Executable(val method: KFunction<*>,
                      val instance: Command,
                      val arguments: List<Argument>,
                      private val contextParameter: KParameter
)
{
    open suspend fun execute(ctx: CommandContext, args: HashMap<KParameter, Any?>, complete: suspend (Boolean, Throwable?) -> Unit)
    {
        method.instanceParameter?.let { args[it] = instance }
        args[contextParameter] = ctx

        kotlin.runCatching {
            when {
                method.isSuspend -> method.callSuspendBy(args)
                else -> method.callBy(args)
            } }
            .onSuccess { complete(true, null) }
            .onFailure { complete(false, it.cause ?: it) }
    }
}