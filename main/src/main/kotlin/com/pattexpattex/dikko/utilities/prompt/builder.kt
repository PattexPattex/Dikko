package com.pattexpattex.dikko.utilities.prompt

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.emoji.toEmoji
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.messages.MessageEditData
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun prompt(
    text: String,
    timeout: Duration = 5.minutes,
    options: List<PromptOption> = emptyList(),
    filter: (ButtonInteractionEvent) -> Boolean = { true },
    builder: PromptBuilder.() -> Unit = {}
) = PromptBuilder(text, timeout, options, filter).apply(builder)

@PromptDSL
class PromptBuilder internal constructor(
    var text: String,
    var timeout: Duration,
    options: List<PromptOption>,
    var filter: (ButtonInteractionEvent) -> Boolean
) {
    val options = OptionsAccumulator(options)

    fun options(builder: OptionsAccumulator.() -> Unit) {
        options.apply(builder)
    }

    fun filter(filter: (ButtonInteractionEvent) -> Boolean) {
        this.filter = filter
    }

    suspend fun build(message: Message) = createPrompt(message.author) { message.editMessage(it).setReplace(true) }

    suspend fun build(hook: InteractionHook) = createPrompt(hook.interaction.user) { hook.editOriginal(it).setReplace(true) }

    suspend fun build(callback: IReplyCallback, ephemeral: Boolean = false) = build(callback.deferReply(ephemeral).await())

    suspend fun build(callback: IMessageEditCallback) = build(callback.deferEdit().await())

    private fun createPrompt(author: User, editFun: (MessageEditData) -> RestAction<Message>): Prompt {
        require(text.isNotEmpty()) { "Text cannot be empty." }
        require(timeout.isPositive()) { "Timeout cannot be negative." }
        require(options.values.isNotEmpty()) { "Prompt must contain at least 1 page." }

        return Prompt(
            Random.nextInt(Int.MAX_VALUE).toString(),
            timeout,
            text,
            options.values,
            editFun,
            author.jda,
            author.idLong,
            filter
        ).apply { start() }
    }
}

@PromptDSL
class OptionsAccumulator(values: List<PromptOption>) {
    internal val values = values.toMutableList()

    init {
        require(values.size < 5) { "Max. 5 options allowed." }
    }

    fun cancel() {
        require(values.size < 5) { "Max. 5 options allowed." }

        values += PromptOption("cancel", "Cancel prompt", ButtonStyle.DANGER, "🗑".toEmoji(), 1)
    }

    fun option(
        id: String,
        text: String? = null,
        style: ButtonStyle = ButtonStyle.PRIMARY,
        emoji: Emoji? = null,
        requiredSelects: Int = 1
    ) {
        require(values.size < 5) { "Max. 5 options allowed." }

        values += PromptOption(id, text ?: id, style, emoji ?: values.size.toEmoji(), requiredSelects)
    }

    private fun Int.toEmoji(): Emoji = when (this) {
        0 -> "1️⃣"
        1 -> "2️⃣"
        2 -> "3️⃣"
        3 -> "4️⃣"
        4 -> "5️⃣"
        else -> throw IllegalStateException()
    }.toEmoji()
}

@DslMarker
internal annotation class PromptDSL