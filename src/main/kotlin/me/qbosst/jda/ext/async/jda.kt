package me.qbosst.jda.ext.async

import dev.minn.jda.ktx.await
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.sharding.ShardManager

suspend fun JDA.getOrRetrieveUserById(id: Long): User? = getUserById(id) ?: retrieveUserById(id).await()

suspend fun JDA.getOrRetrieveUserById(id: String): User? = getUserById(id) ?: retrieveUserById(id).await()

suspend fun ShardManager.getOrRetrieveUserById(id: Long): User? = getUserById(id) ?: retrieveUserById(id).await()

suspend fun ShardManager.getOrRetrieveUserById(id: String): User? = getUserById(id) ?: retrieveUserById(id).await()

