package com.pattexpattex.dikko.internal.implementation.generic

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.internal.event.wrapper.EventWrapperImpl
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent

internal class GenericEventWrapper private constructor(e: GenericEvent, c: GuildContext, k: Dikko, p: Path) : EventWrapperImpl(e, c, k, p) {
    override fun getJDA(): JDA = throw UnsupportedOperationException()
}