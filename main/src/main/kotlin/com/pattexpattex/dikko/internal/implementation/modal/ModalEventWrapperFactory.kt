package com.pattexpattex.dikko.internal.implementation.modal

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactory
import com.pattexpattex.dikko.internal.path.PathFactory
import com.pattexpattex.dikko.internal.path.PathImpl
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

internal class ModalEventWrapperFactory : EventWrapperFactory {
    override fun create(
        event: GenericEvent,
        ctx: GuildContext,
        dikko: Dikko,
        eventHandler: EventHandlerProxy?
    ): EventWrapper {
        return ModalEventWrapper(
            event as ModalInteractionEvent,
            ctx,
            dikko,
            PathFactory.create<ModalEventWrapper>(event.modalId, eventHandler?.pathMatcher) as PathImpl
        )
    }
}