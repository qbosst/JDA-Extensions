package me.qbosst.jda.ext.commands.impl

import me.qbosst.jda.ext.commands.entities.PrefixProvider
import net.dv8tion.jda.api.entities.Message

class DefaultPrefixProvider(private val prefixes: List<String>, private val allowMentionPrefix: Boolean): PrefixProvider
{
    override fun provide(message: Message): Collection<String>
    {
        val prefixes = mutableListOf<String>()

        if(allowMentionPrefix) {
            val selfUserId = message.jda.selfUser.id
            prefixes.add("<@${selfUserId}>")
            prefixes.add("<@!${selfUserId}>")
        }

        prefixes.addAll(prefixes)

        return prefixes
    }
}