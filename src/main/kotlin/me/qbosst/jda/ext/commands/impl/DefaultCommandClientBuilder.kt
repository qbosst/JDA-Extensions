package me.qbosst.jda.ext.commands.impl

import me.qbosst.jda.ext.commands.argument.ArgumentParser
import me.qbosst.jda.ext.commands.entities.CommandEventListener
import me.qbosst.jda.ext.commands.entities.PrefixProvider
import me.qbosst.jda.ext.commands.parsers.*
import net.dv8tion.jda.api.entities.*
import java.net.URL
import java.time.Duration

class DefaultCommandClientBuilder
{
    private var prefixes: List<String> = emptyList()
    private var allowMentionPrefix: Boolean = true
    private var prefixProvider: PrefixProvider? = null
    private val listeners: MutableList<CommandEventListener> = mutableListOf()
    private val developerIds: MutableSet<Long> = mutableSetOf()

    fun setPrefixes(vararg prefixes: String) = also { this.prefixes = prefixes.toList() }

    fun setPrefixProvider(prefixProvider: PrefixProvider) = also { this.prefixProvider = prefixProvider }

    fun setDeveloperIds(vararg developerIds: Long) = also {
        this.developerIds.clear()
        this.developerIds.addAll(developerIds.toList())
    }

    fun setAllowMentionPrefix(allowMentionPrefix: Boolean) = also { this.allowMentionPrefix = allowMentionPrefix }

    fun addEventListeners(vararg listeners: CommandEventListener) = also { this.listeners.addAll(listeners) }

    fun addDeveloperIds(vararg developerIds: Long) = also { this.developerIds.addAll(developerIds.toList()) }

    fun registerParser(clazz: Class<*>, parser: Parser<*>) = also { ArgumentParser.parsers[clazz] = parser }

    inline fun <reified T> registerParser(parser: Parser<T>) = also { registerParser(T::class.java, parser) }

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

    fun build(): DefaultCommandClient
    {
        val prefixProvider = this.prefixProvider ?: DefaultPrefixProvider(prefixes, allowMentionPrefix)
        val client = DefaultCommandClient(prefixProvider, listeners, developerIds)
        return client
    }
}