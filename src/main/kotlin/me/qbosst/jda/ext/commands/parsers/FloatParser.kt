package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.time.Duration
import java.util.*

class FloatParser: Parser<Float>
{
    override suspend fun parse(ctx: CommandContext, param: String) = Optional.ofNullable(param.toFloatOrNull())

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<Float>, List<String>>
    {
        val successful = mutableListOf<Float>()
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