package com.pattexpattex.dikko.internal.implementation.button

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactory
import com.pattexpattex.dikko.internal.path.PathFactory
import com.pattexpattex.dikko.internal.path.PathImpl
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

internal class ButtonEventWrapperFactory : EventWrapperFactory {
    override fun create(event: GenericEvent, ctx: GuildContext, dikko: Dikko, eventHandler: EventHandlerProxy?) =
        ButtonEventWrapper(
            event as ButtonInteractionEvent,
            ctx,
            dikko,
            PathFactory.create<ButtonEventWrapper>(event.componentId, eventHandler?.pathMatcher) as PathImpl
        )
}