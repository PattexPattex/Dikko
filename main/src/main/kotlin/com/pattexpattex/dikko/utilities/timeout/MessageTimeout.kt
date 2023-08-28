package com.pattexpattex.dikko.utilities.timeout

import dev.minn.jda.ktx.messages.edit
import dev.minn.jda.ktx.util.ref
import kotlinx.coroutines.CoroutineScope
import net.dv8tion.jda.api.entities.Message
import kotlin.time.Duration

class MessageTimeout internal constructor(
    id: String,
    message: Message,
    scope: CoroutineScope,
    timeout: Duration
) : Timeout(id, timeout, scope, {}) {
    private val channel by message.guildChannel.ref()
    val message get() = channel.retrieveMessageById(messageId)
    val messageId = message.id

    override suspend fun doAction() {
        message.flatMap { msg ->
            msg.edit(replace = false, components = msg.components.map { it.asDisabled() })
        }.queue({}) {
            log.warn("Failed editing message", it)
        }
    }
}
