package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.Context
import java.util.*

interface Parser<T>
{
    suspend fun parse(ctx: Context, param: String): Optional<T>

    /**
     * @return [Pair] of [Array] of [T], containing all the successful parsed arguments and [List] of [String],
     * containing all the failed parsed arguments
     */
    suspend fun parse(ctx: Context, params: List<String>): Pair<Array<T>, List<String>>

    companion object
    {
        suspend inline fun <reified T> defaultParse(
            parser: Parser<T>,
            ctx: Context,
            params: List<String>
        ): Pair<Array<T>, List<String>> {
            val successful = mutableListOf<T>()
            val unSuccessful = mutableListOf<String>()
            params.forEach { param ->
                val optional = parser.parse(ctx, param)
                if(optional.isPresent)
                {
                    successful.add(optional.get())
                }
                else
                {
                    unSuccessful.add(param)
                }
            }

            return Pair(successful.toTypedArray(), unSuccessful)
        }
    }
}