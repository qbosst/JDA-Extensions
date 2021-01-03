package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*

class IntParser: Parser<Int>
{
    override suspend fun parse(ctx: CommandContext, param: String) = Optional.ofNullable(param.toIntOrNull())
}