package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import net.dv8tion.jda.api.entities.ISnowflake
import java.time.Duration
import java.util.*
import java.util.regex.Pattern

class SnowflakeParser: Parser<ISnowflake>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<ISnowflake>
    {
        val matcher = snowflakeMatcher.matcher(param)
        return when
        {
            matcher.matches() ->
                (matcher.group("sid") ?: matcher.group("id")).toLong().let { id -> Optional.of(ISnowflake { id }) }
            else ->
                Optional.empty()
        }
    }

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<ISnowflake>, List<String>>
    {
        val successful = mutableListOf<ISnowflake>()
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
        private val snowflakeMatcher = Pattern.compile("^(?:<(?:@!?|@&|#)(?<sid>[0-9]{17,21})>|(?<id>[0-9]{17,21}))")
    }
}