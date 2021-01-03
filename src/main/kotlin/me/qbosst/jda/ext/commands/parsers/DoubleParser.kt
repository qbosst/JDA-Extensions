package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*

class DoubleParser: Parser<Double>
{
    override suspend fun parse(ctx: CommandContext, param: String) = Optional.ofNullable(param.toDoubleOrNull())
}