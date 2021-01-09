package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.entities.IContext
import net.dv8tion.jda.api.entities.Role
import java.util.*

class RoleParser: Parser<Role>
{
    override suspend fun parse(ctx: IContext, param: String): Optional<Role>
    {
        val snowflake = snowflakeParser.parse(ctx, param)

        return when
        {
            snowflake.isPresent -> Optional.ofNullable(ctx.guild?.getRoleById(snowflake.get().idLong))

            else -> Optional.ofNullable(ctx.guild?.getRolesByName(param, false)?.firstOrNull())
        }
    }

    override suspend fun parse(ctx: IContext, params: List<String>) = Parser.defaultParse(this, ctx, params)

    companion object
    {
        private val snowflakeParser = SnowflakeParser()
    }
}