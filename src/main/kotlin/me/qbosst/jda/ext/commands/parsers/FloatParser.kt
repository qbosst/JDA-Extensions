package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.Context
import java.util.*

class FloatParser: Parser<Float>
{
    override suspend fun parse(ctx: Context, param: String) = Optional.ofNullable(param.toFloatOrNull())

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)
}