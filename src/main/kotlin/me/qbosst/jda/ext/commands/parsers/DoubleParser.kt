package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*

class DoubleParser: Parser<Double>
{
    override suspend fun parse(ctx: CommandContext, param: String) = Optional.ofNullable(param.toDoubleOrNull())

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<Double>, List<String>>
    {
        val successful = mutableListOf<Double>()
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