package com.pattexpattex.dikko.utilities.listeners

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.api.path.PathMatcher
import com.pattexpattex.dikko.internal.DikkoImpl
import com.pattexpattex.dikko.internal.EventPropertyExtractor
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactoryFactory
import com.pattexpattex.dikko.internal.path.PathFactory
import com.pattexpattex.dikko.internal.path.PathImpl
import com.pattexpattex.dikko.internal.path.matcher.PathMatcherFactory
import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.events.toTimeout
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import kotlin.reflect.KClass
import kotlin.time.Duration

inline fun <reified T : EventWrapper> Dikko.listener(
    pattern: String,
    timeout: Duration? = null,
    noinline action: suspend CoroutineEventListener.(T) -> Unit
) = listener(T::class, pattern, timeout, action)

fun <T : EventWrapper> Dikko.listener(
    type: KClass<T>,
    pattern: String,
    timeout: Duration? = null,
    action: suspend CoroutineEventListener.(T) -> Unit
): CoroutineEventListener {
    this as DikkoImpl
    ignorePaths(type, pattern)

    return object : CoroutineEventListener {
        override val timeout = timeout.toTimeout()

        override fun cancel() {
            jda.removeEventListener(this)
            handlePaths(pattern)
        }

        @Suppress("unchecked_cast")
        override suspend fun onEvent(event: GenericEvent) {
            if (EventWrapperFactoryFactory.getWrapperClassForEvent(event) != type) {
                return
            }

            val rawPath = when (event) {
                is CommandAutoCompleteInteractionEvent -> "${event.fullCommandName} ${event.focusedOption.name}"
                else -> EventPropertyExtractor.getComponentId(event)!!
            }

            val matcher = PathMatcherFactory.create(type, pattern)
            val path = PathFactory.create(type, rawPath)

            if (!matcher.matches(path)) {
                return
            }

            val guild = EventPropertyExtractor.getGuild(event)
            val ctx = dispatcherManager.getGuildContext(guild)

            val eventWrapper = EventWrapperFactoryFactory
                .createFactory(event)
                .create(event, ctx, this@listener, MockEventHandlerProxy(type, matcher))

            action(eventWrapper as T)
        }
    }.also { jda.addEventListener(it) }
}

/**
 * **Note:** Supports only events supported by Dikko.
 * */
inline fun <reified T : GenericEvent> JDA.listener(
    pattern: String,
    timeout: Duration? = null,
    crossinline action: suspend (Pair<T, Path>) -> Unit
): CoroutineEventListener {
    return listener<T>(timeout) {
        val rawPath = when (it) {
            is CommandAutoCompleteInteractionEvent -> "${it.fullCommandName} ${it.focusedOption.name}"
            else -> EventPropertyExtractor.getComponentId(it) ?: throw IllegalArgumentException("Unsupported event type")
        }

        val type = EventWrapperFactoryFactory.getWrapperClassForEventType<T>()
        val matcher = PathMatcherFactory.create(type, pattern)
        val path = PathFactory.create(type, rawPath)

        if (matcher.matches(path)) {
            action(it to (path as PathImpl).fillSegments(matcher))
        }
    }
}

private class MockEventHandlerProxy(
    override val eventType: KClass<out EventWrapper>,
    override val pathMatcher: PathMatcher<*>
) : EventHandlerProxy {
    override val clazz get() = throw UnsupportedOperationException()
    override val callable get() = throw UnsupportedOperationException()
    override val requiredPermissions get() = throw UnsupportedOperationException()
    override val requiredUserPermissions get() = throw UnsupportedOperationException()
    override val isPrivateOnly get() = throw UnsupportedOperationException()
    override val isGuildOnly get() = throw UnsupportedOperationException()
}
