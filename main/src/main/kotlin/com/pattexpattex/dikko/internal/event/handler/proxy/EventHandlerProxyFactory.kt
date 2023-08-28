package com.pattexpattex.dikko.internal.event.handler.proxy

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.exception.ExceptionTools.wrap
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventHandlerProxy
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

internal object EventHandlerProxyFactory {
    @Suppress("UNCHECKED_CAST")
    fun create(clazz: KClass<*>, callable: KCallable<*>): EventHandlerProxy {
        return when (val eventType = callable.valueParameters[0].type) {
            typeOf<SlashEventWrapper>() -> SlashEventHandlerProxy(clazz, callable)
            else -> {
                if (eventType.isSubtypeOf(typeOf<EventWrapper>())) {
                     EventHandlerProxyImpl(clazz, callable, eventType.classifier as KClass<out EventWrapper>)
                } else throw IllegalArgumentException("Unsupported event type '$eventType'").wrap(clazz, callable)
            }
        }
    }
}