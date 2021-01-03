package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import net.dv8tion.jda.api.entities.TextChannel
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

    companion object
    {
        private val snowflakeParser = SnowflakeParser()
    }
}