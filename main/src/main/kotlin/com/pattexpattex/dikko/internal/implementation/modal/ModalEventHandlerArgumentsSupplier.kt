package com.pattexpattex.dikko.internal.implementation.modal

import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.handler.args.EventHandlerArgumentsSupplierImpl
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters

internal class ModalEventHandlerArgumentsSupplier : EventHandlerArgumentsSupplierImpl(true) {
    override fun supplyWithDefinition(
        eventWrapper: EventWrapper,
        handlerInstance: Any,
        definition: DefinitionProxy<*>,
        eventHandler: EventHandlerProxy
    ): Map<KParameter, Any?> {
        val map = super.supply(eventWrapper, handlerInstance, eventHandler) as HashMap

        if (eventHandler.callable.valueParameters.size == 1) {
            return map
        }

        eventWrapper as ModalEventWrapper
        for (i in eventWrapper.values.indices) {
            val parameter = eventHandler.callable.valueParameters[i + 1]
            val mapping = eventWrapper.values[i]
            map[parameter] = mapping
        }

        return map
    }
}