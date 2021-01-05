package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.Context
import java.util.*

class StringParser: Parser<String>
{
    override suspend fun parse(ctx: Context, param: String): Optional<String> = when
    {
        param.isEmpty() || param.isBlank() -> Optional.empty()
        else -> Optional.of(param)
    }

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)
}