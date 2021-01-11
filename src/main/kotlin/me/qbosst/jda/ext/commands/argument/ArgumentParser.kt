package me.qbosst.jda.ext.commands.argument

import me.qbosst.jda.ext.commands.entities.CommandExecutable
import me.qbosst.jda.ext.commands.entities.IContext
import me.qbosst.jda.ext.commands.exceptions.BadArgument
import me.qbosst.jda.ext.commands.exceptions.ParserNotRegistered
import me.qbosst.jda.ext.commands.parsers.Parser
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KParameter

class ArgumentParser(private val ctx: IContext,
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

    private fun getNextArrayArgument(greedy: Boolean): Pair<List<String>, List<String>>
    {
        val (arguments, original) = when
        {
            args.isEmpty() -> Pair(emptyList(), emptyList())

            greedy ->
            {
                val arguments = mutableListOf<String>()
                val original = arguments.toMutableList()
                while (args.isNotEmpty())
                {
                    val (arg) = getNextArgument(false)
                    if(arg.isEmpty() || arg.isBlank())
                        break
                    arguments.add(arg)
                }

                Pair(arguments as List<String>, original)
            }

            args[0].startsWith('"') && delimiter == ' ' -> parseQuoted()
                .let { (argument, original) -> Pair(listOf(argument), original) }

            else -> take(1).let { taken -> Pair(taken, taken) }
        }
        return Pair(arguments, original)
    }

    suspend fun parse(arg: Argument): Any?
    {
        if(arg.type.isArray)
            return parseList(arg)

        val parser = parsers[arg.type]
            ?: throw ParserNotRegistered(arg.type)

        val (argument, original) = getNextArgument(arg.isGreedy)

        val result = if(argument.isEmpty()) Optional.empty() else kotlin.runCatching { parser.parse(ctx, argument) }
            .getOrElse { throw BadArgument(arg, argument, cause = it) }

        // whether we can pass null or the default value
        val canSubstitute = arg.isTentative || ((arg.isOptional || arg.isNullable) && argument.isEmpty())

        if(!result.isPresent && !canSubstitute)
            throw BadArgument(arg, argument)

        if(!result.isPresent && arg.isTentative)
            restore(original)

        return result.orElse(null)
    }

    private suspend fun parseList(arg: Argument): Any?
    {
        val argType = arg.type.componentType

        val parser = parsers[argType]
            ?: throw ParserNotRegistered(argType)

        val (arguments, original) = getNextArrayArgument(arg.isGreedy)

        val (parsed, failedParses) = if(arguments.isEmpty()) Pair(emptyArray(), emptyList()) else kotlin.runCatching { parser.parse(ctx, arguments) }
            .getOrElse { throw BadArgument(arg, arguments.joinToString(delimiterStr), cause = it) }

        if(failedParses.isNotEmpty() && !arg.isTentative)
            throw BadArgument(arg, failedParses)

        // whether we can pass null or the default value
        val canSubstitute = arg.isTentative || ((arg.isOptional || arg.isNullable) && arguments.isEmpty())

        if(parsed.isEmpty() && !canSubstitute)
            throw BadArgument(arg, arguments)

        if(parsed.isEmpty() && arg.isTentative)
            restore(original)

        return if(parsed.isEmpty()) null else parsed
    }

    companion object
    {
        val parsers = hashMapOf<Class<*>, Parser<*>>()

        suspend fun parseArguments(executable: CommandExecutable, ctx: IContext, _args: List<String>,
                                   delimiter: Char): HashMap<KParameter, Any?>
        {
            if(executable.arguments.isEmpty())
                return hashMapOf()

            val args = if(delimiter == ' ') _args else _args.joinToString(" ").split(delimiter).toMutableList()
            val parser = ArgumentParser(ctx, delimiter, args)

            val resolved = hashMapOf<KParameter, Any?>()

            executable.arguments.forEach { arg ->
                val result = parser.parse(arg)

                if(result != null || (arg.isNullable && !arg.isOptional) || (arg.isTentative && arg.isNullable))
                {
                    resolved[arg.kParameter] = result
                }
            }

            return resolved
        }
    }
}