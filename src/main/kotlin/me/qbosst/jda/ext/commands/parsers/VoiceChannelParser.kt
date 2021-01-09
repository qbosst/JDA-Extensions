package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.IContext
import net.dv8tion.jda.api.entities.VoiceChannel
import java.util.*

class VoiceChannelParser: Parser<VoiceChannel>
{
    override suspend fun parse(ctx: IContext, param: String): Optional<VoiceChannel>
    {
        val snowflake = snowflakeParser.parse(ctx, param)

        return when
        {
            snowflake.isPresent -> Optional.ofNullable(ctx.guild?.getVoiceChannelById(snowflake.get().idLong))

            else -> Optional.ofNullable(ctx.guild?.getVoiceChannelsByName(param, false)?.firstOrNull())
        }
    }

    override suspend fun parse(ctx: IContext, params: List<String>) = Parser.defaultParse(this, ctx, params)

    companion object
    {
        private val snowflakeParser = SnowflakeParser()
    }
}