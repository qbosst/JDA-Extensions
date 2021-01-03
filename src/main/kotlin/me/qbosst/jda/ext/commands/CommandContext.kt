package me.qbosst.jda.ext.commands

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class CommandContext(val client: CommandClient,
                     val event: MessageReceivedEvent,
                     val prefix: String,
                     val command: Command)
{
    val jda get() = event.jda
    val message get() = event.message
    val author get() = event.author
    val guild get() = if(event.isFromGuild) event.guild else null
    val member get() = event.member
    val textChannel get() = if(event.isFromType(ChannelType.TEXT)) event.textChannel else null
    val privateChannel get() =  if(event.isFromType(ChannelType.PRIVATE)) event.privateChannel else null
    val messageChannel get() = event.channel

    fun reply(text: CharSequence) = messageChannel.sendMessage(text)

    fun reply(embed: MessageEmbed) = messageChannel.sendMessage(embed)

    fun reply(msg: Message) = messageChannel.sendMessage(msg)


}