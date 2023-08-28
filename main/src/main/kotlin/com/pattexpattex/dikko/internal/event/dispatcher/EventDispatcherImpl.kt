package com.pattexpattex.dikko.internal.event.dispatcher

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.annotations.GuildOnly
import com.pattexpattex.dikko.api.annotations.PrivateOnly
import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.dispatcher.EventDispatcher
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.EventPropertyExtractor
import com.pattexpattex.dikko.internal.contentToString
import com.pattexpattex.dikko.internal.ctx.GuildContextImpl
import com.pattexpattex.dikko.internal.definition.proxy.DefinitionProxyImpl
import com.pattexpattex.dikko.internal.event.handler.args.EventHandlerArgumentsSupplierFactory
import com.pattexpattex.dikko.internal.event.handler.failure.EventHandlerFailure
import com.pattexpattex.dikko.internal.event.handler.failure.EventHandlerFailureFactory
import com.pattexpattex.dikko.internal.event.wrapper.EventWrapperImpl
import com.pattexpattex.dikko.internal.exception.ExceptionTools
import com.pattexpattex.dikko.internal.exception.ExceptionTools.details
import dev.minn.jda.ktx.util.SLF4J
import net.dv8tion.jda.api.JDA

open class EventDispatcherImpl<T : Any> internal constructor(override val dikko: Dikko) : EventDispatcher<T> {
    protected val log by SLF4J

    internal val eventHandlerProxies = hashSetOf<EventHandlerProxy>()
    internal val definitionProxies = hashSetOf<DefinitionProxy<T>>()
    final override val proxies: Map<String, DefinitionProxy<T>> by lazy { Proxies(this) }

    @Suppress("name_shadowing")
    internal open suspend fun dispatch(event: EventWrapper): Any? {
        val eventHandlerProxy = eventHandlerProxies.find { it.pathMatcher.matches(event.path) }
        if (eventHandlerProxy == null) {
            log.warn("No event handler found for event", details(event = event))
            return EventHandlerFailureFactory.eventHandler(dikko, event.path)
        }

        val selfFail = selfPermissionCheck(event, eventHandlerProxy)
        if (selfFail != null) {
            return selfFail
        }

        val userFail = userPermissionCheck(event, eventHandlerProxy)
        if (userFail != null) {
            return userFail
        }

        val availFail = actionAvailableCheck(event, eventHandlerProxy)
        if (availFail != null) {
            return availFail
        }

        val event = (event as EventWrapperImpl).createWithHandlerProxy(eventHandlerProxy) // cheap hack
        val handlerInstance = (event.ctx as GuildContextImpl).getHandlerInstance(eventHandlerProxy.clazz)
        val argumentsSupplier = EventHandlerArgumentsSupplierFactory.create(eventHandlerProxy.eventType)

        val args = if (argumentsSupplier.needsDefinition) {
            val definitionProxy = definitionProxies.find { it.path == event.path }
            if (definitionProxy == null) {
                log.warn("No definition found for event", details(event = event))
                return EventHandlerFailureFactory.definition(dikko, event.path)
            }

            argumentsSupplier.supplyWithDefinition(event, handlerInstance, definitionProxy, eventHandlerProxy)
        } else {
            argumentsSupplier.supply(event, handlerInstance, eventHandlerProxy)
        }

        val result = eventHandlerProxy.call(args)

        return if (!result.isSuccess) {
            log.warn("Event handler invocation failed", result.exceptionOrNull())
            EventHandlerFailureFactory.exception(dikko, result.exceptionOrNull())
        } else {
            result.getOrNull()
        }
    }

    internal open fun registerEventHandler(proxy: EventHandlerProxy) {
        val old = eventHandlerProxies.find { it.pathMatcher == proxy.pathMatcher }
        if (old != null) {
            return log.warn("Event handler with path '${proxy.pathMatcher}' is defined twice!\n" +
                    "\tConflicting definitions: ${ExceptionTools.format(old.clazz, old.callable)}\n" +
                    "\tAnd: ${ExceptionTools.format(proxy.clazz, proxy.callable)}")
        }

        if (proxy.isGuildOnly && proxy.isPrivateOnly) {
            return log.warn(
                "Event handler is annotated by ${GuildOnly::class.simpleName} and ${PrivateOnly::class.simpleName}",
                details(proxy.clazz, proxy.callable)
            )
        }

        eventHandlerProxies.add(proxy)
    }

    @Suppress("unchecked_cast")
    internal open fun registerDefinition(proxy: DefinitionProxy<*>) {
        proxy as DefinitionProxyImpl<*>
        val old = definitionProxies.find { it.path == proxy.path } as DefinitionProxyImpl<*>?
        if (old != null) {
            log.warn("Object with path '${proxy.path}' is defined twice!\n" +
                    "\tConflicting definitions: ${ExceptionTools.format(old.visitor.clazz, old.visitor.callable)}\n" +
                    "\tAnd: ${ExceptionTools.format(proxy.visitor.clazz, proxy.visitor.callable)}")
        } else {
            definitionProxies.add(proxy as DefinitionProxy<T>)
        }
    }

    internal open suspend fun finalizeSetup(jda: JDA) {}

    private fun selfPermissionCheck(
        event: EventWrapper,
        proxy: EventHandlerProxy
    ): EventHandlerFailure? {
        if (EventPropertyExtractor.getGuild(event) == null) {
            return null
        }

        val channel = EventPropertyExtractor.getGuildChannel(event)
        if (channel == null) {
            log.warn("Cannot perform self-user permission check, dropping event", details(event = event))
            return EventHandlerFailureFactory.create(dikko)
        }

        val permissions = proxy.requiredPermissions
        if (permissions.isEmpty()) {
            return null
        }

        val selfMember = channel.guild.selfMember
        if (selfMember.hasPermission(permissions)) {
            return null
        }

        val missing = permissions - selfMember.permissions
        log.warn(
            "Self user has insufficient permissions; Missing: ${missing.contentToString()}",
            details(proxy.clazz, proxy.callable, event)
        )
        return EventHandlerFailureFactory.selfPerms(dikko, missing)
    }

    private fun userPermissionCheck(
        event: EventWrapper,
        proxy: EventHandlerProxy
    ): EventHandlerFailure? {
        val member = EventPropertyExtractor.getMember(event) ?: return null

        val permissions = proxy.requiredUserPermissions

        if (permissions.isEmpty() || member.hasPermission(permissions)) {
            return null
        }

        val missing = permissions - member.permissions
        log.warn(
            "User has insufficient permissions; Missing: ${missing.contentToString()}",
            details(proxy.clazz, proxy.callable, event)
        )
        return EventHandlerFailureFactory.userPerms(dikko, missing)
    }

    private fun actionAvailableCheck(
        event: EventWrapper,
        proxy: EventHandlerProxy
    ): EventHandlerFailure? {
        if (!proxy.isPrivateOnly && !proxy.isGuildOnly) {
            return null
        }

        val guild = EventPropertyExtractor.getGuild(event)
        return if ((guild != null && proxy.isPrivateOnly) || (guild == null && proxy.isGuildOnly)) {
            EventHandlerFailureFactory.unavailable(dikko, proxy.isGuildOnly, proxy.isPrivateOnly)
        } else {
            null
        }
    }

    private class Proxies<T : Any>(private val eventDispatcher: EventDispatcherImpl<T>) : AbstractMap<String, DefinitionProxy<T>>() {
        override operator fun get(key: String): DefinitionProxy<T> = eventDispatcher.definitionProxies.find { it.path.value == key }!!
        override val entries: Set<Map.Entry<String, DefinitionProxy<T>>> get() = eventDispatcher.definitionProxies.map { Entry(it) }.toSet()
        override val size get() = eventDispatcher.definitionProxies.size

        private inner class Entry(
            override val value: DefinitionProxy<T>
        ) : Map.Entry<String, DefinitionProxy<T>> {
            override val key: String = value.path.value
        }
    }
}