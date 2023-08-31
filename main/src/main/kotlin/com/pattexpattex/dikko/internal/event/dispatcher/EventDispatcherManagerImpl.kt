package com.pattexpattex.dikko.internal.event.dispatcher

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.dispatcher.EventDispatcherManager
import com.pattexpattex.dikko.internal.DikkoImpl
import com.pattexpattex.dikko.internal.EventPropertyExtractor
import com.pattexpattex.dikko.internal.ctx.GuildContextFactory
import com.pattexpattex.dikko.internal.ctx.GuildContextImpl
import com.pattexpattex.dikko.internal.definition.DefinitionArgumentsSupplier
import com.pattexpattex.dikko.internal.definition.visitor.DefinitionVisitorFactory
import com.pattexpattex.dikko.internal.event.handler.proxy.EventHandlerProxyFactory
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactoryFactory
import dev.minn.jda.ktx.util.SLF4J
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.GenericEvent
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

internal class EventDispatcherManagerImpl(override val dikko: Dikko) : EventDispatcherManager {
    private val log by SLF4J
    val dispatchersMap: MutableMap<KClass<out EventWrapper>, EventDispatcherImpl<*>> = hashMapOf()
    val guildContexts: MutableMap<Long, GuildContextImpl> = hashMapOf()

    suspend fun onEvent(event: GenericEvent): Any? {
        if (EventPropertyExtractor.getUser(event)?.isBot == true) {
            return null
        }

        val wrapperClass = EventWrapperFactoryFactory.getWrapperClassForEvent(event)
        val dispatcher = dispatchersMap[wrapperClass] ?: return null

        val guild = EventPropertyExtractor.getGuild(event)
        val ctx = getGuildContext(guild)
        val wrapper = EventWrapperFactoryFactory.createFactory(event).create(event, ctx, dikko, null)

        return if ((dikko as DikkoImpl).ignorePaths.any { it.matches(wrapper.path) }) {
//            log.info("Ignoring event", details(event = wrapper))
            null
        } else {
            dispatcher.dispatch(wrapper)
        }
    }

    fun getGuildContext(guild: Guild?): GuildContextImpl {
        return guildContexts.getOrPut(guild?.idLong ?: -1) {
            GuildContextFactory.create(guild, dikko)
        }
    }

    suspend fun registerDefinition(clazz: KClass<*>, callable: KCallable<Any>) {
        val visitor = DefinitionVisitorFactory.create(clazz, callable)
        val dispatcher = getEventDispatcher(visitor.eventType)

        val args = DefinitionArgumentsSupplier.supply(clazz, callable, (dikko as DikkoImpl).dispatcherManager.getGuildContext(null))
        val proxy = visitor.call(args)

        if (proxy.isFailure) {
            return log.error("Failed to retrieve definition '${visitor.path.value}'", proxy.exceptionOrNull())
        }

        dispatcher.registerDefinition(proxy.getOrThrow())
    }

    fun registerEventHandler(clazz: KClass<*>, callable: KCallable<*>) {
        val handler = EventHandlerProxyFactory.create(clazz, callable)
        val dispatcher = getEventDispatcher(handler.eventType)

        dispatcher.registerEventHandler(handler)
    }

    suspend fun runFinalizerTasks(jda: JDA) = coroutineScope {
        for ((_, dispatcher) in dispatchersMap) {
            launch {
                try {
                    dispatcher.finalizeSetup(jda)
                } catch (e: Throwable) {
                    log.error("Postprocessing task threw an exception in a coroutine", e)
                }
            }
        }
    }

    private fun getEventDispatcher(eventType: KClass<out EventWrapper>): EventDispatcherImpl<*> {
        return dispatchersMap.getOrPut(eventType) {
            EventDispatcherFactory.create(eventType, dikko) as EventDispatcherImpl<*>
        }
    }

    override val ctx: EventDispatcherManager.CTX by lazy { EventDispatcherManager.CTX(this) }
    override val dispatchers: EventDispatcherManager.Dispatchers by lazy { EventDispatcherManager.Dispatchers(this) }
}