package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.IContext
import java.util.*

class FloatParser: Parser<Float>
{
    override suspend fun parse(ctx: IContext, param: String) = Optional.ofNullable(param.toFloatOrNull())

    override suspend fun parse(ctx: IContext, params: List<String>) = Parser.defaultParse(this, ctx, params)
}