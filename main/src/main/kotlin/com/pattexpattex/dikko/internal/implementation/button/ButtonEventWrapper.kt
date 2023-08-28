package com.pattexpattex.dikko.internal.implementation.button

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.internal.event.wrapper.EventWrapperImpl
import com.pattexpattex.dikko.internal.path.PathImpl
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction

class ButtonEventWrapper internal constructor(
    event: ButtonInteractionEvent,
    ctx: GuildContext,
    dikko: Dikko,
    path: PathImpl
) : EventWrapperImpl(event, ctx, dikko, path), ButtonInteraction by event
