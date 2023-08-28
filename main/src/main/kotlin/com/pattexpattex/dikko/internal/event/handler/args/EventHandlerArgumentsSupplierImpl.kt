package com.pattexpattex.dikko.internal.event.handler.args

import com.pattexpattex.dikko.api.event.handler.EventHandlerArgumentsSupplier
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.api.event.EventWrapper
import kotlin.reflect.KParameter

internal open class EventHandlerArgumentsSupplierImpl(override val needsDefinition: Boolean = false) : EventHandlerArgumentsSupplier {
    override fun supply(eventWrapper: EventWrapper, handlerInstance: Any, eventHandler: EventHandlerProxy): Map<KParameter, Any?> {
        return hashMapOf(eventHandler.callable.parameters[0] to handlerInstance, eventHandler.callable.parameters[1] to eventWrapper)
    }
}