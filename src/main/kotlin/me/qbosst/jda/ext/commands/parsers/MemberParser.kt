package me.qbosst.jda.ext.commands.parsers

import dev.minn.jda.ktx.await
import me.qbosst.jda.ext.commands.entities.Context
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import java.util.*

class MemberParser: Parser<Member>
{
    override suspend fun parse(ctx: Context, param: String): Optional<Member>
    {
        val snowflake = snowflakeParser.parse(ctx, param)

        return when
        {
            snowflake.isPresent -> Optional.ofNullable(ctx.guild?.retrieveMemberById(snowflake.get().idLong)?.await())

            User.USER_TAG.matcher(param).matches() -> Optional.ofNullable(ctx.guild?.getMemberByTag(param))

            else -> Optional.ofNullable(ctx.guild?.memberCache
                ?.first { member -> (member.user.name == param) || (member.nickname?.equals(param) == true) })
        }
    }

    override suspend fun parse(ctx: Context, params: List<String>) = Parser.defaultParse(this, ctx, params)

    companion object
    {
        private val snowflakeParser = SnowflakeParser()
    }

}