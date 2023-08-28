package com.pattexpattex.dikko.api.event.handler

import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.api.event.EventWrapper
import kotlin.reflect.KParameter

internal interface EventHandlerArgumentsSupplier {
    val needsDefinition: Boolean
    fun supply(eventWrapper: EventWrapper, handlerInstance: Any, eventHandler: EventHandlerProxy): Map<KParameter, Any?>
    fun supplyWithDefinition(
        eventWrapper: EventWrapper,
        handlerInstance: Any,
        definition: DefinitionProxy<*>,
        eventHandler: EventHandlerProxy
    ): Map<KParameter, Any?> {
        return supply(eventWrapper, handlerInstance, eventHandler)
    }
}