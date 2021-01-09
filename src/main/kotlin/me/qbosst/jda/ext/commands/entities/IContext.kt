package me.qbosst.jda.ext.commands.entities

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface IContext {
    val client: ICommandClient
    val event: MessageReceivedEvent
    val command: Command

    val jda: JDA
    val message: Message
    val author: User
    val guild: Guild?
    val member: Member?
    val textChannel: TextChannel?
    val privateChannel: PrivateChannel?
    val messageChannel: MessageChannel
}
