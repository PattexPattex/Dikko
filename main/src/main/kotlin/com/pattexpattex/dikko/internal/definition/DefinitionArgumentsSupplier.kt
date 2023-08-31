package com.pattexpattex.dikko.internal.definition

import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.internal.event.handler.instances.EventHandlerInstanceFactoryFactory
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

internal object DefinitionArgumentsSupplier {
    fun supply(clazz: KClass<*>, callable: KCallable<*>, ctx: GuildContext): Map<KParameter, Any?> {
        val instance = EventHandlerInstanceFactoryFactory.createFactory(clazz).createInstance(ctx)
        return mapOf(callable.parameters[0] to instance)
    }
}