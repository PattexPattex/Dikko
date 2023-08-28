package com.pattexpattex.dikko.internal.implementation.slash

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.wrapper.EventWrapperImpl
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactory
import com.pattexpattex.dikko.internal.path.PathFactory
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction

class SlashEventWrapper private constructor(
    event: SlashCommandInteractionEvent,
    ctx: GuildContext,
    dikko: Dikko,
    path: SlashPath
) : EventWrapperImpl(event, ctx, dikko, path), SlashCommandInteraction by event {
    internal class Factory : EventWrapperFactory {
        override fun create(event: GenericEvent, ctx: GuildContext, dikko: Dikko, eventHandler: EventHandlerProxy?) =
            SlashEventWrapper(
                event as SlashCommandInteractionEvent,
                ctx,
                dikko,
                PathFactory.create<SlashEventWrapper>(event.fullCommandName, eventHandler?.pathMatcher) as SlashPath
            )
    }
}