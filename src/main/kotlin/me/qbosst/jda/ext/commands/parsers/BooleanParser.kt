package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import java.util.*
import java.util.regex.Pattern

class BooleanParser: Parser<Boolean>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<Boolean> = when
    {
        trueExpression.matcher(param).matches() -> Optional.of(true)
        falseExpression.matcher(param).matches() -> Optional.of(false)
        else -> Optional.empty()
    }

    companion object
    {
        val trueExpression = Pattern.compile("(y(es)?)|(t(rue)?)|(1)|(on)|(enable)")
        val falseExpression = Pattern.compile("(no?)|(f(alse)?)|(0)|(off)|(disable)")
    }
}