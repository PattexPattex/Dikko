package com.pattexpattex.dikko.internal

import dev.minn.jda.ktx.messages.InlineMessage
import dev.minn.jda.ktx.messages.MentionConfig
import dev.minn.jda.ktx.messages.Mentions
import dev.minn.jda.ktx.messages.MessageEditBuilder
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData

internal inline fun <reified T> Collection<T>.contentToString() = this.toTypedArray().contentToString()

@Suppress("FunctionName")
internal inline fun MessageEdit(data: MessageEditData, builder: InlineMessage<MessageEditData>.() -> Unit): MessageEditData = MessageEditBuilder(
    data.content,
    data.embeds,
    data.files,
    data.components,
    Mentions(
        MentionConfig.users(data.mentionedUsers.map(String::toLong)),
        MentionConfig.roles(data.mentionedRoles.map(String::toLong)),
        MentionType.EVERYONE in data.allowedMentions,
        MentionType.HERE in data.allowedMentions
    ),
    MessageEditBuilder.from(data).isReplace,
    builder
).build()

