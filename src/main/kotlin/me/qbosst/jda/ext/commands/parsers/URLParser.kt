package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.net.URL
import java.util.*

class URLParser: Parser<URL>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<URL> =
        kotlin.runCatching { Optional.of(URL(param)) }.getOrElse { Optional.empty() }
}