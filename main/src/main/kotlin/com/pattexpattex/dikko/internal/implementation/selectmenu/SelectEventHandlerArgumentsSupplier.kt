package com.pattexpattex.dikko.internal.implementation.selectmenu

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.handler.args.EventHandlerArgumentsSupplierImpl
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters

internal class SelectEventHandlerArgumentsSupplier : EventHandlerArgumentsSupplierImpl(false) {
    override fun supply(
        eventWrapper: EventWrapper,
        handlerInstance: Any,
        eventHandler: EventHandlerProxy
    ): Map<KParameter, Any?> {
        val map = super.supply(eventWrapper, handlerInstance, eventHandler) as HashMap

        val parameter = eventHandler.callable.valueParameters.getOrNull(1)
        if (parameter != null) {
            val values = when (eventWrapper) {
                is StringSelectEventWrapper -> eventWrapper.selectedOptions
                is EntitySelectEventWrapper -> eventWrapper.values
                else -> throw IllegalStateException()
            }

            map[parameter] = values
        }

        return map
    }
}