package com.pattexpattex.dikko.internal.implementation.slashgroup

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.internal.event.wrapper.EventWrapperImpl
import com.pattexpattex.dikko.internal.path.PathImpl
import net.dv8tion.jda.api.events.GenericEvent

class GroupEventWrapper private constructor(
    event: GenericEvent,
    ctx: GuildContext,
    dikko: Dikko,
    path: PathImpl
) : EventWrapperImpl(event, ctx, dikko, path), GenericEvent {
    override fun getJDA() = event.jda
}