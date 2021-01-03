package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*

class FloatParser: Parser<Float>
{
    override suspend fun parse(ctx: CommandContext, param: String) = Optional.ofNullable(param.toFloatOrNull())
}