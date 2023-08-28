package com.pattexpattex.dikko.internal.implementation.contextmenu

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.wrapper.EventWrapperImpl
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactory
import com.pattexpattex.dikko.internal.path.PathFactory
import com.pattexpattex.dikko.internal.path.PathImpl
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction

class MessageContextMenuEventWrapper internal constructor(
    event: MessageContextInteractionEvent,
    ctx: GuildContext,
    dikko: Dikko,
    path: PathImpl
) : EventWrapperImpl(event, ctx, dikko, path), MessageContextInteraction by event {
    internal class Factory : EventWrapperFactory {
        override fun create(
            event: GenericEvent,
            ctx: GuildContext,
            dikko: Dikko,
            eventHandler: EventHandlerProxy?
        ): EventWrapper = MessageContextMenuEventWrapper(
            event as MessageContextInteractionEvent,
            ctx,
            dikko,
            PathFactory.create<MessageContextMenuEventWrapper>(event.fullCommandName, eventHandler?.pathMatcher) as PathImpl
        )
    }
}