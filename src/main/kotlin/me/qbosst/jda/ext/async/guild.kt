package me.qbosst.jda.ext.async

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.GenericMessageEvent

suspend fun Guild.retrieveMembersByEffectiveName(name: String, ignoreCase: Boolean = true): List<Member> =
    findMembers { it.effectiveName.equals(name, ignoreCase) }.await()

suspend fun Guild.retrieveMembersByNickname(name: String, ignoreCase: Boolean = true): List<Member> =
    findMembers { it.nickname?.equals(name, ignoreCase) == true }.await()

suspend fun Guild.retrieveMembersByName(name: String, ignoreCase: Boolean = true): List<Member> =
    findMembers { it.user.name.equals(name, ignoreCase) }.await()

val GenericMessageEvent.nullableGuild: Guild?
    get() = if(isFromGuild) guild else null

val GenericMessageEvent.nullableTextChannel: TextChannel?
    get() = if(channelType == ChannelType.TEXT) textChannel else null


