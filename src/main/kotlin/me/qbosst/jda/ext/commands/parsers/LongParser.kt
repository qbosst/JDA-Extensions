package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*

class LongParser: Parser<Long>
{
    override suspend fun parse(ctx: CommandContext, param: String) = Optional.ofNullable(param.toLongOrNull())
}