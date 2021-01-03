package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*

class StringParser: Parser<String>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<String> = when
    {
        param.isEmpty() || param.isBlank() -> Optional.empty()
        else -> Optional.of(param)
    }
}