package me.qbosst.jda.ext.commands

import me.qbosst.jda.ext.commands.argument.ArgumentParser
import me.qbosst.jda.ext.commands.entities.PrefixProvider
import me.qbosst.jda.ext.commands.hooks.CommandEventListener
import me.qbosst.jda.ext.commands.parsers.*
import net.dv8tion.jda.api.entities.*
import java.net.URL
import java.time.Duration

class CommandClientBuilder
{
    private var ignoreBots: Boolean = true
    private val listeners: MutableCollection<CommandEventListener> = mutableListOf()
    private val developerIds: MutableCollection<Long> = mutableListOf()

    private var prefixProvider: PrefixProvider = object: PrefixProvider
    {
        override fun provide(message: Message): Collection<String> = listOf()
    }

    fun setIgnoreBots(ignoreBots: Boolean) = apply { this.ignoreBots = ignoreBots }

    fun setDeveloperIds(vararg ids: Long) = apply {
        developerIds.clear()
        developerIds.addAll(ids.toTypedArray())
    }

    fun addDeveloperIds(vararg ids: Long) = apply { developerIds.addAll(ids.toTypedArray()) }

    fun addEventListeners(vararg listeners: CommandEventListener) = apply { this.listeners.addAll(listeners) }

    fun setPrefixProvider(prefixProvider: PrefixProvider) = apply { this.prefixProvider = prefixProvider }

    fun registerDefaultParsers() = also {
        // Kotlin types and primitives
        val booleanParser = BooleanParser()
        ArgumentParser.parsers[Boolean::class.java] = booleanParser
        ArgumentParser.parsers[java.lang.Boolean::class.java] = booleanParser

        val doubleParser = DoubleParser()
        ArgumentParser.parsers[Double::class.java] = doubleParser
        ArgumentParser.parsers[java.lang.Double::class.java] = doubleParser

        val floatParser = FloatParser()
        ArgumentParser.parsers[Float::class.java] = floatParser
        ArgumentParser.parsers[java.lang.Float::class.java] = floatParser

        val intParser = IntParser()
        ArgumentParser.parsers[Int::class.java] = intParser
        ArgumentParser.parsers[java.lang.Integer::class.java] = intParser

        val longParser = LongParser()
        ArgumentParser.parsers[Long::class.java] = longParser
        ArgumentParser.parsers[java.lang.Long::class.java] = longParser

        // JDA entities
        ArgumentParser.parsers[Member::class.java] = MemberParser()
        ArgumentParser.parsers[Role::class.java] = RoleParser()
        ArgumentParser.parsers[TextChannel::class.java] = TextChannelParser()
        ArgumentParser.parsers[User::class.java] = UserParser()
        ArgumentParser.parsers[VoiceChannel::class.java] = VoiceChannelParser()

        // Custom entities
        ArgumentParser.parsers[String::class.java] = StringParser()
        ArgumentParser.parsers[ISnowflake::class.java] = SnowflakeParser()
        ArgumentParser.parsers[URL::class.java] = URLParser()
        ArgumentParser.parsers[Duration::class.java] = DurationParser()
    }

    fun registerParser(clazz: Class<*>, parser: Parser<*>) = also { ArgumentParser.parsers[clazz] = parser }

    fun build(): CommandClient {

        return CommandClient(
            prefixProvider = prefixProvider,
            ignoreBots = ignoreBots,
            listeners = listeners.toList(),
            developerIds = developerIds
        )
    }

}