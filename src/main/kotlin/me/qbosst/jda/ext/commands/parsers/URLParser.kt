package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.IContext
import java.net.URL
import java.util.*

class URLParser: Parser<URL>
{
    override suspend fun parse(ctx: IContext, param: String): Optional<URL> =
        kotlin.runCatching { Optional.of(URL(param)) }.getOrElse { Optional.empty() }

    override suspend fun parse(ctx: IContext, params: List<String>) = Parser.defaultParse(this, ctx, params)
}