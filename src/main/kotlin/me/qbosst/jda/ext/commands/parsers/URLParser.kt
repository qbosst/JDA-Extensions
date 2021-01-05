package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.Context
import java.net.URL
import java.util.*

class URLParser: Parser<URL>
{
    override suspend fun parse(ctx: Context, param: String): Optional<URL> =
        kotlin.runCatching { Optional.of(URL(param)) }.getOrElse { Optional.empty() }

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)
}