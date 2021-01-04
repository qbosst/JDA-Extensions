package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*

interface Parser<T>
{
    suspend fun parse(ctx: CommandContext, param: String): Optional<T>

    /**
     * @return [Pair] of [Array] of [T], containing all the successful parsed arguments and [List] of [String],
     * containing all the failed parsed arguments
     */
    suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<T>, List<String>>
}