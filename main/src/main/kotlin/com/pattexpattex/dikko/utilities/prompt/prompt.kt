package com.pattexpattex.dikko.utilities.prompt

import com.pattexpattex.dikko.utilities.listeners.await
import com.pattexpattex.dikko.utilities.timeout.Timeout
import dev.minn.jda.ktx.events.getDefaultScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.messages.MessageEditData
import kotlin.time.Duration

class Prompt internal constructor(
    private val id: String,
    val timeout: Duration,
    val text: String,
    val options: List<PromptOption>,
    private val editFun: (MessageEditData) -> RestAction<Message>,
    private val jda: JDA,
    private val authorId: Long,
    private val filter: (ButtonInteractionEvent) -> Boolean,
    private val messageSupplier: PromptMessageSupplier
) {
    private val _scope = getDefaultScope()
    private val _timeout = Timeout("Prompt $id", timeout, _scope, ::timeout)

    private val _finalResult = CompletableDeferred<PromptResult>(_scope.coroutineContext.job)

    /**
     * A [Deferred] result containing the user interaction that resulted in completion of this prompt.
     * If this prompt is cancelled via [cancel] or [timeout], this Deferred will be [cancelled][Deferred.cancel].
     *
     * **Note:** [PromptResult.event] is `null` if invocation of [selectOption] resulted in completion of this prompt.
     */
    val result: Deferred<PromptResult> = _finalResult

    private val _channel = Channel<PromptResult>(capacity = Channel.UNLIMITED)

    /**
     * A channel used to receive individual interactions when users click this prompt.
     * If this prompt is cancelled via [cancel] or [timeout], this Channel will be [cancelled][Channel.cancel].
     */
    val channel: ReceiveChannel<PromptResult> = _channel

    private val inputs = mutableListOf<Long>()

    /**
     * `True` if this prompt completed successfully or was cancelled or timed out. `False` otherwise.
     */
    val isCompleted get() = _finalResult.isCompleted

    internal fun start() {
        _timeout.start()

        _scope.launch {
            while (true) {
                if (isCompleted) {
                    break
                }

                editFun(messageSupplier.activeMessage(id, options, text, _timeout)).queue()

                val (event, path) = jda.await<ButtonInteractionEvent>("dikko.prompt:$id.{id}")

                val id = path.parameters["id"]!!.value
                val option = options[id]
                val type = when (id) {
                    "cancel" -> PromptResult.Type.CANCEL
                    else -> PromptResult.Type.RESPONSE
                }

                PromptResult(option, event, type).let {
                    _channel.send(it)
                    onResult(it)
                }
            }
        }
    }

    /**
     * Mocks a user selecting an option. This interaction is not sent to the [channel].
     */
    fun selectOption(id: String) {
        if (isCompleted) {
            return
        }

        val option = options[id] ?: return
        onResult(PromptResult(option, null, PromptResult.Type.RESPONSE))

        if (!isCompleted) {
            editFun(messageSupplier.activeMessage(id, options, text, _timeout)).queue()
        }
    }

    /**
     * Cancels this prompt. Cancels [channel] and [result].
     */
    fun cancel() {
        if (!isCompleted) {
            onResult(PromptResult(null, null, PromptResult.Type.CANCEL))
        }
    }

    /**
     * Times out, effectively cancels this prompt. Cancels [channel] and [result].
     */
    fun timeout() {
        if (!isCompleted) {
            onResult(PromptResult(null, null, PromptResult.Type.TIMEOUT))
        }
    }

    private fun onResult(result: PromptResult) {
        when (result.type) {
            PromptResult.Type.RESPONSE -> {
                if (result.event != null) {
                    if (result.event.user.idLong in inputs) {
                        return result.event
                            .reply(messageSupplier.alreadyRespondedMessage(options, text, _timeout))
                            .setEphemeral(true)
                            .queue()
                    }
                    if (!filter(result.event)) {
                        return result.event
                            .reply(messageSupplier.cannotInteractMessage(options, text, _timeout))
                            .setEphemeral(true)
                            .queue()
                    }
                }

                onResponse(result)
            }
            PromptResult.Type.CANCEL -> {
                if (result.event?.user?.idLong != authorId) {
                    result.event
                        ?.reply(messageSupplier.cannotCancelMessage(options, text, _timeout))
                        ?.setEphemeral(true)
                        ?.queue()
                } else {
                    complete(result)
                }
            }
            PromptResult.Type.TIMEOUT -> complete(result)
        }
    }

    private fun onResponse(result: PromptResult) {
        result.event?.let { inputs += it.user.idLong }

        if (++result._option!!.selects < result._option.requiredSelects) {
            result.event?.deferEdit()?.queue()
        } else {
            complete(result)
        }
    }

    private fun complete(result: PromptResult) {
        fun cancelCoroutines(msg: String) {
            CancellationException(msg).let {
                _scope.cancel(it)
                _channel.cancel(it)
            }
        }

        when (result.type) {
            PromptResult.Type.RESPONSE -> _finalResult.complete(result)
            PromptResult.Type.CANCEL -> cancelCoroutines("Prompt was cancelled")
            PromptResult.Type.TIMEOUT -> cancelCoroutines("Prompt timed out")
        }

        if (result.event != null) {
            messageSupplier.completedMessage(result, text)
                ?.let { result.event.editMessage(it).setReplace(true).queue() }
        } else {
            messageSupplier.completedMessage(result, text)?.let { editFun(it).queue() }
        }

        _channel.close()
    }
}

class PromptOption(
    val id: String,
    val text: String,
    val style: ButtonStyle,
    val emoji: Emoji,
    val requiredSelects: Int
) {
    var selects: Int = 0
        internal set
}

class PromptResult(
    internal val _option: PromptOption?,
    val event: ButtonInteractionEvent?,
    val type: Type
) {
    val option get() = _option!!

    enum class Type {
        RESPONSE,
        CANCEL,
        TIMEOUT
    }
}

operator fun List<PromptOption>.get(id: String) = find { it.id == id }