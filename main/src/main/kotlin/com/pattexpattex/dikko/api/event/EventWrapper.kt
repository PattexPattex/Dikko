package com.pattexpattex.dikko.api.event

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.path.Path
import net.dv8tion.jda.api.events.GenericEvent

interface EventWrapper : GenericEvent {
    val ctx: GuildContext
    val dikko: Dikko
    val path: Path
}
