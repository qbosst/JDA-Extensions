package me.qbosst.jda.ext.async

import dev.minn.jda.ktx.await
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

suspend fun Guild.getOrRetrieveMemberById(id: Long): Member? = getMemberById(id) ?: retrieveMemberById(id).await()

suspend fun Guild.getOrRetrieveMemberById(id: String): Member? = getMemberById(id) ?: retrieveMemberById(id).await()

suspend fun Guild.getOrRetrieveMember(user: User): Member? = getMember(user) ?: retrieveMember(user).await()

suspend fun Guild.retrieveMembersByEffectiveName(name: String, ignoreCase: Boolean = true): List<Member> =
    findMembers { it.effectiveName.equals(name, ignoreCase) }.await()

suspend fun Guild.retrieveMembersByNickname(name: String, ignoreCase: Boolean = true): List<Member> =
    findMembers { it.nickname?.equals(name, ignoreCase) == true }.await()

suspend fun Guild.retrieveMembersByName(name: String, ignoreCase: Boolean = true): List<Member> =
    findMembers { it.user.name.equals(name, ignoreCase) }.await()



