package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.async.await
import me.qbosst.jda.ext.commands.entities.IContext
import net.dv8tion.jda.api.entities.User
import java.util.*

class UserParser: Parser<User>
{
    override suspend fun parse(ctx: IContext, param: String): Optional<User>
    {
        val snowflake = snowflakeParser.parse(ctx, param)
        
        return when
        {
            ctx.guild != null -> memberParser.parse(ctx, param).map { member -> member.user }

            snowflake.isPresent -> Optional.ofNullable(ctx.jda.retrieveUserById(snowflake.get().idLong).await())

            User.USER_TAG.matcher(param).matches() -> Optional.ofNullable(ctx.jda.getUserByTag(param))

            else -> Optional.ofNullable(ctx.jda.getUsersByName(param, false).firstOrNull())
        }
    }

    override suspend fun parse(ctx: IContext, params: List<String>) = Parser.defaultParse(this, ctx, params)

    companion object
    {
        private val snowflakeParser = SnowflakeParser()
        private val memberParser = MemberParser()
    }

}