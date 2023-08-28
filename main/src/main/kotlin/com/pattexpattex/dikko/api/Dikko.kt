package com.pattexpattex.dikko.api

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerFailureMessageSupplier
import com.pattexpattex.dikko.internal.DikkoImpl
import com.pattexpattex.dikko.internal.event.handler.failure.DefaultEventHandlerFailureMessageSupplier
import com.pattexpattex.dikko.internal.event.handler.failure.FunctionalEventHandlerFailureMessageSupplier
import dev.minn.jda.ktx.events.CoroutineEventManager
import net.dv8tion.jda.api.JDA
import kotlin.reflect.KClass

/**
 * The base of Dikko.
 * */
interface Dikko {
    val jda: JDA

    /**
     * Stops Dikko from receiving and handling events.
     */
    fun shutdown()

    /**
     * Add event paths for Dikko to ignore. These events will not be handled until the paths are removed by [handlePaths].
     * @param type Type of events to ignore.
     * @see com.pattexpattex.dikko.api.ignorePaths
     */
    fun ignorePaths(type: KClass<out EventWrapper>, vararg patterns: String)

    /**
     * Remove a previously ignored event path. These events will be received and handled.
     * @see ignorePaths
     */
    fun handlePaths(vararg patterns: String)

    companion object {
        /**
         * Builder function for Dikko.
         * @sample com.pattexpattex.dikko.samples.create
         */
        fun create(
            jda: JDA,
            packageName: String,
            failureMessages: EventHandlerFailureMessageSupplier = DefaultEventHandlerFailureMessageSupplier(),
            additionalEventHandlers: List<KClass<*>> = listOf(),
            builder: Builder.() -> Unit = {}
        ): Dikko {
            if (jda.eventManager !is CoroutineEventManager) {
                throw IllegalArgumentException("EventManager used by JDA is not CoroutineEventManager!")
            }

            return Builder(
                jda,
                arrayListOf(packageName),
                failureMessages,
                additionalEventHandlers.toMutableList()
            ).apply(builder).build()
        }
    }

    class Builder internal constructor(
        var jda: JDA,
        val packages: MutableList<String>,
        var failureMessages: EventHandlerFailureMessageSupplier,
        val additionalEventHandlers: MutableList<KClass<*>>
    ) {
        fun failureMessages(block: FunctionalEventHandlerFailureMessageSupplier.() -> Unit) {
            val supplier = when (failureMessages) {
                is FunctionalEventHandlerFailureMessageSupplier -> failureMessages as FunctionalEventHandlerFailureMessageSupplier
                else -> FunctionalEventHandlerFailureMessageSupplier()
            }

            failureMessages = supplier.apply(block)
        }

        fun build(): Dikko = DikkoImpl(
            jda,
            packages,
            additionalEventHandlers,
            failureMessages
        ).also { jda.addEventListener(it) }
    }
}