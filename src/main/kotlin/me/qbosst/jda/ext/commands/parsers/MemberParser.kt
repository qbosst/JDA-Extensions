package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.async.getOrRetrieveMemberById
import me.qbosst.jda.ext.commands.CommandContext
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import java.time.Duration
import java.util.*

class MemberParser: Parser<Member>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<Member>
    {
        val snowflake = snowflakeParser.parse(ctx, param)

        return when
        {
            snowflake.isPresent -> Optional.ofNullable(ctx.guild?.getOrRetrieveMemberById(snowflake.get().idLong))

            User.USER_TAG.matcher(param).matches() -> Optional.ofNullable(ctx.guild?.getMemberByTag(param))

            else -> Optional.ofNullable(ctx.guild?.memberCache
                ?.first { member -> (member.user.name == param) || (member.nickname?.equals(param) == true) })
        }
    }

    override suspend fun parse(ctx: CommandContext, params: List<String>): Pair<Array<Member>, List<String>>
    {
        val successful = mutableListOf<Member>()
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