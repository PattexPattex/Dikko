package com.pattexpattex.dikko.internal.event.handler.args

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerArgumentsSupplier
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventHandlerArgumentsSupplier
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.ContextMenuEventHandlerArgumentsSupplier
import com.pattexpattex.dikko.internal.implementation.contextmenu.MessageContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.UserContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventHandlerArgumentsSupplier
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.EntitySelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.SelectEventHandlerArgumentsSupplier
import com.pattexpattex.dikko.internal.implementation.selectmenu.StringSelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventHandlerArgumentsSupplier
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import kotlin.reflect.KClass

internal object EventHandlerArgumentsSupplierFactory {
    inline fun <reified T : EventWrapper> create() = create(T::class)

    fun create(eventType: KClass<out EventWrapper>): EventHandlerArgumentsSupplier {
        return when (eventType) {
            SlashEventWrapper::class -> SlashEventHandlerArgumentsSupplier()
            AutocompleteEventWrapper::class -> AutocompleteEventHandlerArgumentsSupplier()
            ModalEventWrapper::class -> ModalEventHandlerArgumentsSupplier()
            StringSelectEventWrapper::class, EntitySelectEventWrapper::class -> SelectEventHandlerArgumentsSupplier()
            MessageContextMenuEventWrapper::class, UserContextMenuEventWrapper::class -> ContextMenuEventHandlerArgumentsSupplier()
            else -> EventHandlerArgumentsSupplierImpl()
        }
    }
}