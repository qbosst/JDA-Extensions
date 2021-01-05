package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.async.getOrRetrieveUserById
import me.qbosst.jda.ext.commands.entities.Context
import net.dv8tion.jda.api.entities.User
import java.util.*

class UserParser: Parser<User>
{
    override suspend fun parse(ctx: Context, param: String): Optional<User>
    {
        val snowflake = snowflakeParser.parse(ctx, param)

        return when
        {
            snowflake.isPresent -> Optional.ofNullable(ctx.jda.getOrRetrieveUserById(snowflake.get().idLong))

            User.USER_TAG.matcher(param).matches() -> Optional.ofNullable(ctx.jda.getUserByTag(param))

            else -> Optional.ofNullable(ctx.jda.getUsersByName(param, false).firstOrNull())
        }
    }

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)

    companion object
    {
        private val snowflakeParser = SnowflakeParser()
    }

}