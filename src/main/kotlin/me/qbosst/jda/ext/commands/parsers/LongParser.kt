package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.Context
import java.util.*

class LongParser: Parser<Long>
{
    override suspend fun parse(ctx: Context, param: String) = Optional.ofNullable(param.toLongOrNull())

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)
}