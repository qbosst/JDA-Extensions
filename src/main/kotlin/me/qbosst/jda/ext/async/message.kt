package me.qbosst.jda.ext.async

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.RestAction

fun Message.addReactions(vararg unicodes: String): RestAction<Void> {
    require(unicodes.isNotEmpty())

    var restAction: RestAction<Void> = addReaction(unicodes.first())
    unicodes.drop(1).forEach { unicode ->
        restAction = restAction.and(addReaction(unicode))
    }

    return restAction
}
