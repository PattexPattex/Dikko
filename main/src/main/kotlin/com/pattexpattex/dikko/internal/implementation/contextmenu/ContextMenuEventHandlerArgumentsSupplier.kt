package com.pattexpattex.dikko.internal.implementation.contextmenu

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.handler.args.EventHandlerArgumentsSupplierImpl
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.context.ContextInteraction
import java.lang.IllegalStateException
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

internal class ContextMenuEventHandlerArgumentsSupplier : EventHandlerArgumentsSupplierImpl(false) {
    override fun supply(
        eventWrapper: EventWrapper,
        handlerInstance: Any,
        eventHandler: EventHandlerProxy
    ): Map<KParameter, Any?> {
        val map = super.supply(eventWrapper, handlerInstance, eventHandler) as HashMap

        val parameter = eventHandler.callable.valueParameters.getOrNull(1)
        if (parameter != null) {
            if ((eventWrapper is UserContextMenuEventWrapper && parameter.type != typeOf<User>()) ||
                (eventWrapper is MessageContextMenuEventWrapper && parameter.type != typeOf<Message>())) {
                throw IllegalStateException("")
            }

            map[parameter] = (eventWrapper as ContextInteraction<*>).target
        }

        return map
    }
}