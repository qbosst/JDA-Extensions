package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import net.dv8tion.jda.api.entities.TextChannel
import java.time.Duration
import java.util.*

class TextChannelParser: Parser<TextChannel>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<TextChannel>
    {
        val snowflake = snowflakeParser.parse(ctx, param)

        return when
        {
            snowflake.isPresent -> Optional.ofNullable(ctx.guild?.getTextChannelById(snowflake.get().idLong))

            else -> Optional.ofNullable(ctx.guild?.getTextChannelsByName(param, false)?.firstOrNull())
        }
    }

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<TextChannel>, List<String>>
    {
        val successful = mutableListOf<TextChannel>()
        val unSuccessful = mutableListOf<String>()
        params.forEach { param ->
            val optional = parse(ctx, param)
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

    companion object
    {
        private val snowflakeParser = SnowflakeParser()
    }
}