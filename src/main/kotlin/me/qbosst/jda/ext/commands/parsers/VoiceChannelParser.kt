package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import net.dv8tion.jda.api.entities.VoiceChannel
import java.util.*

class VoiceChannelParser: Parser<VoiceChannel>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<VoiceChannel>
    {
        val snowflake = snowflakeParser.parse(ctx, param)

        return when
        {
            snowflake.isPresent -> Optional.ofNullable(ctx.guild?.getVoiceChannelById(snowflake.get().idLong))

            else -> Optional.ofNullable(ctx.guild?.getVoiceChannelsByName(param, false)?.firstOrNull())
        }
    }

    companion object
    {
        private val snowflakeParser = SnowflakeParser()
    }
}