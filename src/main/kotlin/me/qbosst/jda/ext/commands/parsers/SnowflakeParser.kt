package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.Context
import net.dv8tion.jda.api.entities.ISnowflake
import java.util.*
import java.util.regex.Pattern

class SnowflakeParser: Parser<ISnowflake>
{
    override suspend fun parse(ctx: Context, param: String): Optional<ISnowflake>
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

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)

    companion object
    {
        private val snowflakeMatcher = Pattern.compile("^(?:<(?:@!?|@&|#)(?<sid>[0-9]{17,21})>|(?<id>[0-9]{17,21}))")
    }
}