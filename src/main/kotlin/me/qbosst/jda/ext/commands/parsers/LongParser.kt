package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.IContext
import java.util.*

class LongParser: Parser<Long>
{
    override suspend fun parse(ctx: IContext, param: String) = Optional.ofNullable(param.toLongOrNull())

    override suspend fun parse(ctx: IContext, params: List<String>) = Parser.defaultParse(this, ctx, params)
}