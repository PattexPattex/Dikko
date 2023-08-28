package com.pattexpattex.dikko.internal.event.handler.proxy

import com.pattexpattex.dikko.api.annotations.*
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.api.path.PathMatcher
import com.pattexpattex.dikko.internal.exception.ExceptionTools.wrap
import com.pattexpattex.dikko.internal.exception.ReflectCallException
import com.pattexpattex.dikko.internal.path.matcher.PathMatcherFactory
import net.dv8tion.jda.api.Permission
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible

open class EventHandlerProxyImpl internal constructor(
    override val clazz: KClass<*>,
    override val callable: KCallable<*>,
    override val eventType: KClass<out EventWrapper>
) : EventHandlerProxy {
    override val pathMatcher: PathMatcher<*> = getPathMatcher()
    override val requiredPermissions: List<Permission> get() = findAnnotation<RequirePermissions>(callable, clazz)?.permissions?.toList() ?: emptyList()
    override val requiredUserPermissions: List<Permission> get() = findAnnotation<RequireUserPermissions>(callable, clazz)?.permissions?.toList() ?: emptyList()
    override val isPrivateOnly: Boolean get() = hasAnnotation<PrivateOnly>(callable, clazz)
    override val isGuildOnly: Boolean get() = hasAnnotation<GuildOnly>(callable, clazz)

    override suspend fun call(args: Map<KParameter, Any?>): Result<*> {
        return try {
            callable.isAccessible = true
            val out = callable.callSuspendBy(args)
            Result.success(out)
        } catch (e: Throwable) {
            Result.failure<Nothing>(ReflectCallException("An exception occurred when calling event handler", e).wrap(clazz, callable))
        }
    }

    @JvmName("getPathMatcherFun")
    private fun getPathMatcher(): PathMatcher<*> {
        val annotation = callable.findAnnotation<EventHandler>() ?: throw NullPointerException().wrap(clazz, callable)
        return PathMatcherFactory.create(eventType, annotation.pathPattern)
    }
}