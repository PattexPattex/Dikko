package com.pattexpattex.dikko.utilities.prompt

import com.pattexpattex.dikko.utilities.timeout.Timeout
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.interactions.components.row
import dev.minn.jda.ktx.messages.MessageCreate
import dev.minn.jda.ktx.messages.MessageEdit
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData

interface PromptMessageSupplier {
    fun activeMessage(id: String, options: List<PromptOption>, text: String, timeout: Timeout): MessageEditData
    fun completedMessage(result: PromptResult, text: String): MessageEditData?
    fun alreadyRespondedMessage(options: List<PromptOption>, text: String, timeout: Timeout): MessageCreateData
    fun cannotInteractMessage(options: List<PromptOption>, text: String, timeout: Timeout): MessageCreateData
    fun cannotCancelMessage(options: List<PromptOption>, text: String, timeout: Timeout): MessageCreateData
}

open class DefaultPromptMessageSupplier : PromptMessageSupplier {
    override fun activeMessage(id: String, options: List<PromptOption>, text: String, timeout: Timeout) = MessageEdit {
        embed {
            title = text
            footer("Expires")
            timestamp = timeout.endTime
            color = 0x36393F

            description = options.filter { it.id != "cancel" }.joinToString("\n") {
                buildString {
                    append("${it.emoji.formatted} ${it.text}")

                    if (it.requiredSelects > 1) {
                        append(" (`${it.selects}` out of `${it.requiredSelects}` required)")
                    }
                }
            }
        }

        components += options.map {
            button("dikko.prompt:$id.${it.id}", style = it.style, emoji = it.emoji)
        }.row()
    }

    override fun completedMessage(result: PromptResult, text: String) = MessageEdit {
        embed {
            color = 0x36393F
            val prefix = when (result.type) {
                PromptResult.Type.RESPONSE -> result.option.emoji.formatted
                PromptResult.Type.CANCEL -> "Cancelled"
                PromptResult.Type.TIMEOUT -> "Timed out"
            }

            description = "$prefix **|** $text"
        }
    }

    override fun alreadyRespondedMessage(options: List<PromptOption>, text: String, timeout: Timeout) = MessageCreate {
        content = "❗ You already responded to this prompt."
    }

    override fun cannotInteractMessage(options: List<PromptOption>, text: String, timeout: Timeout) = MessageCreate {
        content = "❌ You cannot respond to this prompt."
    }

    override fun cannotCancelMessage(options: List<PromptOption>, text: String, timeout: Timeout) = MessageCreate {
        content = "❌ You cannot cancel this prompt."
    }
}