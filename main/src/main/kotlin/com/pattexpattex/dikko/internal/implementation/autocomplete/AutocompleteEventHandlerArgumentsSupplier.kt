package com.pattexpattex.dikko.internal.implementation.autocomplete

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.handler.args.EventHandlerArgumentsSupplierImpl
import net.dv8tion.jda.api.interactions.AutoCompleteQuery
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

internal class AutocompleteEventHandlerArgumentsSupplier : EventHandlerArgumentsSupplierImpl(false) {
    override fun supply(
        eventWrapper: EventWrapper,
        handlerInstance: Any,
        eventHandler: EventHandlerProxy
    ): Map<KParameter, Any?> {
        eventWrapper as AutocompleteEventWrapper
        val map = super.supply(eventWrapper, handlerInstance, eventHandler) as HashMap

        eventHandler.callable.valueParameters.getOrNull(1)?.let {
            if (it.type == typeOf<AutoCompleteQuery>()) {
                map[it] = eventWrapper.focusedOption
            }
        }

        return map
    }
}