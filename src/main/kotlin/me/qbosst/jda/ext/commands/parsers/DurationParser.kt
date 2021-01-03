package me.qbosst.jda.ext.commands.parsers

import me.qbosst.jda.ext.commands.CommandContext
import me.qbosst.jda.ext.util.TimeUtil
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class DurationParser: Parser<Duration>
{
    override suspend fun parse(ctx: CommandContext, param: String): Optional<Duration> =
        when (val seconds = TimeUtil.parseTime(param, TimeUnit.SECONDS))
        {
            null -> Optional.ofNullable(null)
            else -> Optional.of(Duration.ofSeconds(seconds))
        }
}