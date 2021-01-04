package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.time.Duration
import java.util.*

class LongParser: Parser<Long>
{
    override suspend fun parse(ctx: CommandContext, param: String) = Optional.ofNullable(param.toLongOrNull())

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<Long>, List<String>>
    {
        val successful = mutableListOf<Long>()
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