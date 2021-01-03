package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*

interface Parser<T>
{
    suspend fun parse(ctx: CommandContext, param: String): Optional<T>
}