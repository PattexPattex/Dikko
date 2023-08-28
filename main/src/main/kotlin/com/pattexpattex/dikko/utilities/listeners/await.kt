package com.pattexpattex.dikko.utilities.listeners

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.api.path.PathMatcher
import com.pattexpattex.dikko.internal.EventPropertyExtractor
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactoryFactory
import com.pattexpattex.dikko.internal.path.PathFactory
import com.pattexpattex.dikko.internal.path.PathImpl
import com.pattexpattex.dikko.internal.path.matcher.PathMatcherFactory
import dev.minn.jda.ktx.events.await
import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import kotlin.coroutines.resume
import kotlin.reflect.KClass

suspend inline fun <reified T : EventWrapper> Dikko.await(
    pattern: String,
    noinline filter: (T) -> Boolean = { true }
) = await(T::class, pattern, filter)

suspend fun <T : EventWrapper> Dikko.await(
    type: KClass<T>,
    pattern: String,
    filter: (T) -> Boolean = { true }
): T = suspendCancellableCoroutine { cont ->
    listener(type, pattern) {
        if (filter(it)) {
            cancel()
            cont.resume(it)
        }
    }.also { listener ->
        cont.invokeOnCancellation { listener.cancel() }
    }
}

/**
 * **Note:** Supports only events supported by Dikko.
 * */
suspend inline fun <reified T : GenericEvent> JDA.await(
    pattern: String,
    crossinline filter: (T) -> Boolean = { true }
): Pair<T, Path> {
    lateinit var path: Path
    lateinit var matcher: PathMatcher<*>

    val event = await<T> {
        val rawPath = when (it) {
            is CommandAutoCompleteInteractionEvent -> "${it.fullCommandName} ${it.focusedOption.name}"
            else -> EventPropertyExtractor.getComponentId(it)!!
        }
        val type = EventWrapperFactoryFactory.getWrapperClassForEventType<T>()
        matcher = PathMatcherFactory.create(type, pattern)
        path = PathFactory.create(type, rawPath)

        matcher.matches(path) && filter(it)
    }

    return event to (path as PathImpl).fillSegments(matcher)
}
