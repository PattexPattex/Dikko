package com.pattexpattex.dikko.utilities.pagination

import com.pattexpattex.dikko.internal.MessageEdit
import com.pattexpattex.dikko.utilities.listeners.await
import com.pattexpattex.dikko.utilities.timeout.Timeout
import dev.minn.jda.ktx.emoji.toEmoji
import dev.minn.jda.ktx.events.getDefaultScope
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.interactions.components.row
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.messages.MessageEditData
import kotlin.time.Duration

class Pagination internal constructor(
    val id: String,
    val timeout: Duration,
    pages: List<MessageEditData>,
    page: Int,
    private val editFun: (MessageEditData) -> RestAction<Message>,
    private val jda: JDA,
    private val filter: (ButtonInteractionEvent) -> Boolean
) {
    var pages = pages
        set(value) {
            field = value
            selectPage(page)
        }

    var page = page
        private set

    private val _scope = getDefaultScope()
    private val _timeout = Timeout("Pagination $id", timeout, _scope, ::timeout)

    private val _finalResult = CompletableDeferred<ButtonInteractionEvent>(_scope.coroutineContext.job)

    /**
     * A [Deferred] result containing the event that resulted in deletion of this prompt.
     * If this pagination is deleted via [delete] or [timeout], this Deferred will be [cancelled][Deferred.cancel].
     */
    val result: Deferred<ButtonInteractionEvent> = _finalResult

    private val _channel = Channel<ButtonInteractionEvent>(capacity = Channel.UNLIMITED)

    /**
     * A [channel][ReceiveChannel] used to receive individual interactions when users click this pagination.
     * If this pagination is deleted via [delete] or [timeout], this Channel will be [cancelled][Channel.cancel].
     */
    val channel: ReceiveChannel<ButtonInteractionEvent> = _channel

    /**
     * `True` if this pagination was deleted or timed out. `False` otherwise.
     */
    val isCompleted get() = _finalResult.isCompleted

    internal fun start() {
        _timeout.start()

        _scope.launch {
            while (true) {
                editFun(buildPage(page)).queue()

                val (event, path) = jda.await<ButtonInteractionEvent>(
                    "dikko.pagination:$id.{op}",
                    filter
                )
                _channel.send(event)

                when (path.parameters["op"]!!.value) {
                    "prev" -> --page
                    "next" -> ++page
                    "delete" -> {
                        event.deferEdit().queue()
                        _finalResult.complete(event)
                        break
                    }
                }

                event.deferEdit().queue()
            }

            editFun(buildDisabledPage()).queue()
            _channel.close()
        }
    }

    fun updatePages(builder: MutableList<MessageEditData>.() -> Unit) {
        pages = pages.toMutableList().apply(builder)
    }

    /**
     * Mocks a user turning a page. This interaction is not sent to the [channel]
     */
    fun selectPage(page: Int) {
        if (isCompleted) {
            return
        }

        require(page in 0 .. pages.lastIndex) { "Index $page is out of bounds" }
        this.page = page
        editFun(buildPage(page)).queue()
    }

    /**
     * Deletes this pagination. Cancels [channel] and [result].
     */
    fun timeout() {
        doDelete("Pagination timed out")
    }

    /**
     * Deletes this pagination. Cancels [channel] and [result].
     */
    fun delete() {
        doDelete("Pagination deleted programmatically")
    }

    private fun doDelete(msg: String) {
        if (isCompleted) {
            return
        }

        editFun(buildDisabledPage()).queue()
        CancellationException(msg).let {
            _scope.cancel(it)
            _channel.cancel(it)
        }
    }

    private fun buildComponents(forIndex: Int): List<ActionComponent> {
        return listOf(
            button(
                "dikko.pagination:$id.prev",
                emoji = "⬅".toEmoji(),
                style = ButtonStyle.PRIMARY,
                disabled = forIndex == 0
            ),
            button(
                "dikko.pagination:$id.delete",
                emoji = "🗑".toEmoji(),
                style = ButtonStyle.DANGER
            ),
            button(
                "dikko.pagination:$id.next",
                emoji = "➡".toEmoji(),
                style = ButtonStyle.PRIMARY,
                disabled = forIndex == pages.lastIndex
            )
        )
    }

    private fun buildPage(index: Int) = MessageEdit(pages[index]) {
        components += buildComponents(index).row()
    }

    private fun buildDisabledPage() = MessageEdit(pages[page]) {
        components += buildComponents(page).row().asDisabled()
    }
}

fun Pagination.nextPage() = selectPage(page + 1)

fun Pagination.prevPage() = selectPage(page - 1)