package com.pattexpattex.dikko.internal.event.wrapper.factory

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import net.dv8tion.jda.api.events.GenericEvent

internal fun interface EventWrapperFactory {
    fun create(event: GenericEvent, ctx: GuildContext, dikko: Dikko, eventHandler: EventHandlerProxy?): EventWrapper
}