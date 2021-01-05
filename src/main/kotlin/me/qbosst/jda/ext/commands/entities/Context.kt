package me.qbosst.jda.ext.commands.entities

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Context(val client: CommandClient, val event: MessageReceivedEvent, val command: Command)
{
    val jda: JDA = event.jda
    val message: Message = event.message
    val author: User = event.author
    val guild: Guild? = if(event.isFromGuild) event.guild else null
    val member: Member? = if(event.isFromGuild) event.member else null
    val textChannel: TextChannel? = if(event.isFromType(ChannelType.TEXT)) event.textChannel else null
    val privateChannel: PrivateChannel? = if(event.isFromType(ChannelType.PRIVATE)) event.privateChannel else null
    val messageChannel: MessageChannel = event.channel
}