package com.pattexpattex.dikko.internal.implementation.autocomplete

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.wrapper.EventWrapperImpl
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactory
import com.pattexpattex.dikko.internal.implementation.slash.SlashPath
import com.pattexpattex.dikko.internal.path.PathFactory
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction

class AutocompleteEventWrapper internal constructor(
    event: CommandAutoCompleteInteractionEvent,
    ctx: GuildContext,
    dikko: Dikko,
    path: SlashPath
) : EventWrapperImpl(event, ctx, dikko, path), CommandAutoCompleteInteraction by event {
    internal class Factory : EventWrapperFactory {
        override fun create(
            event: GenericEvent,
            ctx: GuildContext,
            dikko: Dikko,
            eventHandler: EventHandlerProxy?
        ): EventWrapper {
            event as CommandAutoCompleteInteractionEvent

            val path = PathFactory.create<AutocompleteEventWrapper>("${event.fullCommandName} ${event.focusedOption.name}", eventHandler?.pathMatcher)
            return AutocompleteEventWrapper(event, ctx, dikko, path as SlashPath)
        }
    }
}