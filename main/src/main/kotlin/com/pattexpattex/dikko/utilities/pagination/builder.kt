package com.pattexpattex.dikko.utilities.pagination

import dev.minn.jda.ktx.coroutines.await
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.messages.MessageEditData
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun pagination(
    timeout: Duration = 5.minutes,
    startingPage: Int = 0,
    pages: List<MessageEditData> = listOf(),
    filter: (ButtonInteractionEvent) -> Boolean = { true },
    builder: PaginationBuilder.() -> Unit = {}
) = PaginationBuilder(pages, startingPage, timeout, filter).apply(builder)

@PaginationDSL
class PaginationBuilder internal constructor(
    pages: List<MessageEditData>,
    var startingPage: Int,
    var timeout: Duration,
    var filter: (ButtonInteractionEvent) -> Boolean
) {
    val pages = PagesAccumulator(pages)

    fun pages(builder: PagesAccumulator.() -> Unit) {
        pages.apply(builder)
    }

    fun filter(filter: (ButtonInteractionEvent) -> Boolean) {
        this.filter = filter
    }

    suspend fun build(message: Message) = createPagination(message.jda) { message.editMessage(it).setReplace(true) }

    suspend fun build(hook: InteractionHook) = createPagination(hook.jda) { hook.editOriginal(it).setReplace(true) }

    suspend fun build(callback: IReplyCallback, ephemeral: Boolean = false) = build(callback.deferReply(ephemeral).await())

    suspend fun build(callback: IMessageEditCallback) = build(callback.deferEdit().await())

    private fun createPagination(jda: JDA, editFun: (MessageEditData) -> RestAction<Message>): Pagination {
        require(pages.values.isNotEmpty()) { "Pagination must contain at least 1 page." }
        require(timeout.isPositive()) { "Timeout cannot be negative." }

        return Pagination(
            Random.nextInt(Int.MAX_VALUE).toString(),
            timeout,
            pages.values,
            startingPage,
            editFun,
            jda,
            filter
        ).apply { start() }
    }
}

@PaginationDSL
class PagesAccumulator internal constructor(pages: List<MessageEditData>) {
    internal val values = pages.toMutableList()

    operator fun plusAssign(page: MessageEditData) { values += page }
    operator fun plusAssign(pages: Collection<MessageEditData>) { values += pages }
    operator fun MessageEditData.unaryPlus() { values += this }
    operator fun Collection<MessageEditData>.unaryPlus() { values += this }
}

@DslMarker
private annotation class PaginationDSL