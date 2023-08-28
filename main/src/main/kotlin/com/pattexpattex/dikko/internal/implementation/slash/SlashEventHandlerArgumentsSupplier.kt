package com.pattexpattex.dikko.internal.implementation.slash

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.handler.args.EventHandlerArgumentsSupplierImpl
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters

internal class SlashEventHandlerArgumentsSupplier : EventHandlerArgumentsSupplierImpl(false) {
    override fun supply(
        eventWrapper: EventWrapper,
        handlerInstance: Any,
        eventHandler: EventHandlerProxy
    ): Map<KParameter, Any?> {
        eventHandler as SlashEventHandlerProxy
        eventWrapper as SlashEventWrapper
        val map = super.supply(eventWrapper, handlerInstance, eventHandler) as HashMap

        for (i in eventHandler.parameters.indices) {
            val parameter = eventHandler.parameters[i]
            val value = eventWrapper.getOptionValue(parameter.optionData.name, parameter.type)
            val kParameter = eventHandler.callable.valueParameters[i + 1]

            if (value == null && !kParameter.isOptional) {
                map[kParameter] = null
            } else if (value != null) {
                map[kParameter] = value
            }
        }

        return map
    }
}