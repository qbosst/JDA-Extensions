package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.time.Duration
import java.util.*

class StringParser: Parser<String>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<String> = when
    {
        param.isEmpty() || param.isBlank() -> Optional.empty()
        else -> Optional.of(param)
    }

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<String>, List<String>>
    {
        val successful = mutableListOf<String>()
        val unSuccessful = mutableListOf<String>()
        params.forEach { param ->
            val optional = parse(ctx, param)
            if(optional.isPresent)
            {
                successful.add(optional.get())
            }
            else
            {
                unSuccessful.add(param)
            }
        }

        return Pair(successful.toTypedArray(), unSuccessful)
    }
}