package com.pattexpattex.dikko.internal.implementation.selectmenu

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.wrapper.EventWrapperImpl
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactory
import com.pattexpattex.dikko.internal.path.PathFactory
import com.pattexpattex.dikko.internal.path.PathImpl
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

class StringSelectEventWrapper internal constructor(
    event: StringSelectInteractionEvent,
    ctx: GuildContext,
    dikko: Dikko,
    path: PathImpl
) : EventWrapperImpl(event, ctx, dikko, path), StringSelectInteraction by event {
    internal class Factory : EventWrapperFactory {
        override fun create(
            event: GenericEvent,
            ctx: GuildContext,
            dikko: Dikko,
            eventHandler: EventHandlerProxy?
        ): EventWrapper =
            StringSelectEventWrapper(
                event as StringSelectInteractionEvent,
                ctx,
                dikko,
                PathFactory.create<StringSelectEventWrapper>(event.componentId, eventHandler?.pathMatcher) as PathImpl
            )
    }
}