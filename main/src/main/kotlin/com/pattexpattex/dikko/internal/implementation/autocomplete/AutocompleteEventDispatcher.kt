package com.pattexpattex.dikko.internal.implementation.autocomplete

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.dispatcher.EventDispatcherImpl
import com.pattexpattex.dikko.internal.exception.ExceptionTools
import net.dv8tion.jda.api.interactions.AutoCompleteQuery
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

class AutocompleteEventDispatcher internal constructor(dikko: Dikko) : EventDispatcherImpl<Nothing>(dikko) {
    override fun registerDefinition(proxy: DefinitionProxy<*>) {}
    override fun registerEventHandler(proxy: EventHandlerProxy) {
        if (checkEventHandlerParameters(proxy)) {
            super.registerEventHandler(proxy)
        }
    }

    private fun checkEventHandlerParameters(proxy: EventHandlerProxy): Boolean {
        if (proxy.callable.valueParameters.size == 1) {
            return true
        }

        val secondParameterExpectedType = typeOf<AutoCompleteQuery>()
        val secondParameter = proxy.callable.valueParameters[1]

        if (secondParameterExpectedType != secondParameter.type) {
            log.warn("{}: Issue(s) with event handler definition:", ExceptionTools.format(proxy.clazz, proxy.callable))
            log.warn("\tInvalid parameter type at index 1 - Expected: $secondParameterExpectedType, actual: '${secondParameter.type}'")
            log.warn("\tDisabling event handler, please fix the issues")
            return false
        }

        return true
    }
}