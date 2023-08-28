package com.pattexpattex.dikko.internal.implementation.generic

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactory
import net.dv8tion.jda.api.events.GenericEvent

internal class GenericEventWrapperFactory : EventWrapperFactory {
    override fun create(event: GenericEvent, ctx: GuildContext, dikko: Dikko, eventHandler: EventHandlerProxy?) =
        throw UnsupportedOperationException()
}
