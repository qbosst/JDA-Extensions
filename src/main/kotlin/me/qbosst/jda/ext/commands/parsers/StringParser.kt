package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.IContext
import java.util.*

class StringParser: Parser<String>
{
    override suspend fun parse(ctx: IContext, param: String): Optional<String> = when
    {
        param.isEmpty() || param.isBlank() -> Optional.empty()
        else -> Optional.of(param)
    }

    override suspend fun parse(ctx: IContext, params: List<String>) = Parser.defaultParse(this, ctx, params)
}