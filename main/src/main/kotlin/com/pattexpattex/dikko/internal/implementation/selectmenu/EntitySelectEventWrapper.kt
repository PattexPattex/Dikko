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
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction

class EntitySelectEventWrapper internal constructor(
    event: EntitySelectInteractionEvent,
    ctx: GuildContext,
    dikko: Dikko,
    path: PathImpl
) : EventWrapperImpl(event, ctx, dikko, path), EntitySelectInteraction by event {
    internal class Factory : EventWrapperFactory {
        override fun create(
            event: GenericEvent,
            ctx: GuildContext,
            dikko: Dikko,
            eventHandler: EventHandlerProxy?
        ): EventWrapper =
            EntitySelectEventWrapper(
                event as EntitySelectInteractionEvent,
                ctx,
                dikko,
                PathFactory.create<EntitySelectEventWrapper>(event.componentId, eventHandler?.pathMatcher) as PathImpl
            )
    }
}