package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.async.getOrRetrieveUserById
import me.qbosst.jda.ext.commands.CommandContext
import net.dv8tion.jda.api.entities.User
import java.time.Duration
import java.util.*

class UserParser: Parser<User>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<User>
    {
        val snowflake = snowflakeParser.parse(ctx, param)

        return when
        {
            snowflake.isPresent -> Optional.ofNullable(ctx.jda.getOrRetrieveUserById(snowflake.get().idLong))

            User.USER_TAG.matcher(param).matches() -> Optional.ofNullable(ctx.jda.getUserByTag(param))

            else -> Optional.ofNullable(ctx.jda.getUsersByName(param, false).firstOrNull())
        }
    }

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<User>, List<String>>
    {
        val successful = mutableListOf<User>()
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