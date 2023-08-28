package com.pattexpattex.dikko.internal.implementation.contextmenu

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.dispatcher.EventDispatcherImpl
import com.pattexpattex.dikko.internal.exception.ExceptionTools
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

class ContextMenuEventDispatcher(dikko: Dikko) : EventDispatcherImpl<CommandData>(dikko) {
    override fun registerEventHandler(proxy: EventHandlerProxy) {
        if (checkEventHandlerParameters(proxy)) {
            super.registerEventHandler(proxy)
        }
    }

    private fun checkEventHandlerParameters(proxy: EventHandlerProxy): Boolean {
        if (proxy.callable.valueParameters.size == 1) {
            return true
        }

        val secondParameterExpectedType = when (proxy.callable.valueParameters[0].type) {
            typeOf<MessageContextMenuEventWrapper>() -> typeOf<Message>()
            typeOf<UserContextMenuEventWrapper>() -> typeOf<User>()
            else -> throw IllegalStateException()
        }

        val secondParameter = proxy.callable.valueParameters[1]

        if (secondParameterExpectedType != secondParameter.type) {
            log.warn("{}: Issue(s) with event handler definition:", ExceptionTools.format(proxy.clazz, proxy.callable))
            log.warn("\tInvalid parameter type at index 1 - Expected: '$secondParameterExpectedType', actual: '${secondParameter.type}'")
            log.warn("\tDisabling event handler, please fix the issues")
            return false
        }

        return true
    }
}