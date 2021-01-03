package me.qbosst.jda.ext.commands.argument

import me.qbosst.jda.ext.commands.CommandContext
import me.qbosst.jda.ext.commands.entities.Executable
import me.qbosst.jda.ext.commands.exceptions.BadArgument
import me.qbosst.jda.ext.commands.exceptions.ParserNotRegistered
import me.qbosst.jda.ext.commands.parsers.Parser
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KParameter

class ArgumentParser(private val ctx: CommandContext,
                     private val delimiter: Char,
                     args: List<String>
)
{
    private val delimiterStr = delimiter.toString()
    private var args = args.toMutableList()

    private fun take(amount: Int) = args.take(amount).onEach { args.removeAt(0) }

    private fun restore(args: List<String>) = this.args.addAll(0, args)

    private fun parseQuoted(): Pair<String, List<String>>
    {
        val iterator = args.joinToString(delimiterStr).iterator()
        val original = StringBuilder()
        val argument = StringBuilder("\"")
        var quoting = false
        var escaping = false

        while(iterator.hasNext())
        {
            val char = iterator.nextChar().also { char -> original.append(char) }

            when
            {
                escaping -> argument.append(char).also { escaping = false }

                char == '\\' -> escaping = true

                char == '"' -> quoting = !quoting

                !quoting && char == delimiter -> if(argument.isEmpty()) continue else break

                else -> argument.append(char)
            }
        }

        argument.append('"')

        val remaining = StringBuilder().apply { iterator.forEachRemaining { char -> append(char) } }
        args = remaining.split(delimiter).toMutableList()
        return Pair(argument.toString(), original.split(delimiter))
    }

    private fun getNextArgument(greedy: Boolean): Pair<String, List<String>>
    {
        val (argument, original) = when
        {
            args.isEmpty() -> Pair("", emptyList())

            greedy -> take(args.size).let { taken -> Pair(taken.joinToString(delimiterStr), taken) }

            args[0].startsWith('"') && delimiter == ' ' -> parseQuoted()

            else -> take(1).let { taken -> Pair(taken.joinToString(delimiterStr), taken) }
        }

        val unquoted = argument.trim().removeSurrounding("\"")
        return Pair(unquoted, original)
    }

    suspend fun parse(arg: Argument): Any?
    {
        val parser = parsers[arg.type]
            ?: throw ParserNotRegistered(arg.type)

        val (argument, original) = getNextArgument(arg.greedy)

        val result = if(argument.isEmpty()) Optional.empty() else kotlin.runCatching { parser.parse(ctx, argument) }
            .getOrElse { throw BadArgument(arg, argument, it) }

        // whether we can pass null or the default value
        val canSubstitute = arg.isTentative || arg.nullable || (arg.optional && argument.isEmpty())

        if(!result.isPresent && !canSubstitute)
            throw BadArgument(arg, argument)

        if(!result.isPresent && arg.isTentative)
            restore(original)

        return result.orElse(null)
    }

    companion object
    {
        val parsers = hashMapOf<Class<*>, Parser<*>>()

        suspend fun parseArguments(executable: Executable, ctx: CommandContext, _args: List<String>,
                                   delimiter: Char): HashMap<KParameter, Any?>
        {
            if(executable.arguments.isEmpty())
                return hashMapOf()

            val args = if(delimiter == ' ') _args else _args.joinToString(" ").split(delimiter).toMutableList()
            val parser = ArgumentParser(ctx, delimiter, args)

            val resolved = hashMapOf<KParameter, Any?>()

            executable.arguments.forEach { arg ->
                val result = parser.parse(arg)

                if(result != null || (arg.nullable && !arg.optional) || (arg.isTentative && arg.nullable))
                    resolved[arg.kParameter] = result
            }

            return resolved
        }
    }
}