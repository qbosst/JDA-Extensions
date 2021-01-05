package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.Context
import java.util.*

class DoubleParser: Parser<Double>
{
    override suspend fun parse(ctx: Context, param: String) = Optional.ofNullable(param.toDoubleOrNull())

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)
}