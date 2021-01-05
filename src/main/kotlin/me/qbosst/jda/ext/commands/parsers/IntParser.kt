package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.Context
import java.util.*

class IntParser: Parser<Int>
{
    override suspend fun parse(ctx: Context, param: String) = Optional.ofNullable(param.toIntOrNull())

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)
}