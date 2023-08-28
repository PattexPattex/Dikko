package com.pattexpattex.dikko.internal

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.annotations.AfterSetup
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerFailureMessageSupplier
import com.pattexpattex.dikko.api.ignorePaths
import com.pattexpattex.dikko.internal.event.dispatcher.EventDispatcherManagerImpl
import com.pattexpattex.dikko.internal.event.handler.failure.EventHandlerFailure
import com.pattexpattex.dikko.internal.event.handler.failure.EventHandlerFailureFactory
import com.pattexpattex.dikko.internal.exception.ExceptionTools.details
import com.pattexpattex.dikko.internal.implementation.button.ButtonEventWrapper
import com.pattexpattex.dikko.internal.path.matcher.PathMatcherFactory
import com.pattexpattex.dikko.internal.path.matcher.PathMatcherImpl
import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.toTimeout
import dev.minn.jda.ktx.messages.editMessage
import dev.minn.jda.ktx.util.SLF4J
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.requests.RestAction
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.valueParameters
import kotlin.time.Duration

internal class DikkoImpl(
    override val jda: JDA,
    packages: List<String>,
    classes: List<KClass<*>>,
    val eventHandlerFailureMessages: EventHandlerFailureMessageSupplier
) : Dikko, CoroutineEventListener {
    private val log by SLF4J<Dikko>()
    val dispatcherManager = EventDispatcherManagerImpl(this)
    override val timeout get() = Duration.INFINITE.toTimeout()

    override fun shutdown() {
        jda.removeEventListener(this)
        cancel()
    }

    private var isCancelled = false
    override fun cancel() {
        isCancelled = true
    }

    val ignorePaths = mutableListOf<PathMatcherImpl<*>>()
    override fun ignorePaths(type: KClass<out EventWrapper>, vararg patterns: String) {
        ignorePaths.addAll(
            patterns.map {
                PathMatcherFactory.create(type, it) as PathMatcherImpl<*>
            }
        )
    }

    override fun handlePaths(vararg patterns: String) {
        ignorePaths.removeIf { it.pattern in patterns }
    }

    init {
        runBlocking {
            launch {
                while (jda.status != JDA.Status.CONNECTED) {
                    if (!jda.status.isInit) {
                        throw IllegalStateException("JDA is shutting down")
                    }

                    delay(50)
                }

                processDefinitions(packages, classes)
                processEventHandlers(packages, classes)
                dispatcherManager.runFinalizerTasks(jda)
                processAfterSetupCallables(packages, classes, jda)

                log.info("Dikko ready!")
            }
        }

        ignorePaths<ButtonEventWrapper>("dikko.prompt:{id}.{key}", "dikko.pagination:{id}.{op}")
        ignorePaths<ButtonEventWrapper>("{nonce}:prev", "{nonce}:next", "{nonce}:delete")
    }

    override suspend fun onEvent(event: GenericEvent) {
        if (isCancelled) { return }

        val out = try {
            dispatcherManager.onEvent(event)
        } catch (e: Throwable) {
            log.error("Failed dispatching event '$event'", e)
            EventHandlerFailureFactory.exception(this, e)
        }

        when (out) {
            is Unit, null -> {}
            is EventHandlerFailure -> processEventHandlerFailure(event, out)
            is RestAction<*> -> out.queue { log.debug("Event handler for event '{}' returned a RestAction, it returned '{}'", event, it) }
            else -> log.debug("Event handler for event '{}' returned '{}'", event, out)
        }
    }

    private suspend fun processDefinitions(packages: List<String>, classes: List<KClass<*>>) {
        for ((clazz, callable) in ClientCallableExtractor.findDefinitionCallables(packages, classes.mapNotNull { it.qualifiedName })) {
            try {
                dispatcherManager.registerDefinition(clazz, callable)
            } catch (e: RuntimeException) {
                log.error("Registering a definition failed", e)
            }
        }
    }

    private fun processEventHandlers(packages: List<String>, classes: List<KClass<*>>) {
        for ((clazz, callable) in ClientCallableExtractor.findAnnotatedCallables<EventHandler>(packages, classes.mapNotNull { it.qualifiedName })) {
            try {
                dispatcherManager.registerEventHandler(clazz, callable)
            } catch (e: RuntimeException) {
                log.error("Registering an event handler failed", e)
            }
        }
    }

    private suspend fun processAfterSetupCallables(packages: List<String>, classes: List<KClass<*>>, jda: JDA) {
        for ((clazz, callable) in ClientCallableExtractor.findAnnotatedCallables<AfterSetup>(packages, classes.mapNotNull { it.qualifiedName })) {
            if (callable.valueParameters.isNotEmpty()) {
                log.warn("Callable is annotated by @${AfterSetup::class.simpleName} but has value parameters", details(clazz, callable))
                continue
            }

            coroutineScope {
                launch {
                    (jda.guilds + null).forEach { guild ->
                        val instance = dispatcherManager.getGuildContext(guild).getHandlerInstance(clazz)

                        try {
                            callable.callSuspend(instance)
                        } catch (e: Throwable) {
                            log.error(
                                "Postprocessing task threw an exception in a coroutine",
                                e.apply { addSuppressed(details(clazz, callable)) }
                            )
                        }

                        if (instance::class.objectInstance != null) {
                            return@forEach
                        }
                    }
                }
            }
        }
    }

    private fun processEventHandlerFailure(event: GenericEvent, failure: EventHandlerFailure) {
        try {
            when (event) {
                is IReplyCallback -> when (event.isAcknowledged) {
                    true -> event.hook.editMessage(content = "_${failure.message}_", replace = true)
                    false -> event.reply(failure.message).setEphemeral(true)
                }.queue()
                is Interaction -> event.messageChannel.sendMessage("_${failure.message}_").queue()
                is MessageReceivedEvent -> event.message.reply("_${failure.message}_").queue()
            }
        } catch (ignore: Throwable) {}
    }
}