package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.net.URL
import java.time.Duration
import java.util.*

class URLParser: Parser<URL>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<URL> =
        kotlin.runCatching { Optional.of(URL(param)) }.getOrElse { Optional.empty() }

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<URL>, List<String>>
    {
        val successful = mutableListOf<URL>()
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