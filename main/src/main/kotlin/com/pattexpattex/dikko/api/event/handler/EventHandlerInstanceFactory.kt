package com.pattexpattex.dikko.api.event.handler

import com.pattexpattex.dikko.api.ctx.GuildContext

fun interface EventHandlerInstanceFactory {
    fun createInstance(ctx: GuildContext): Any
}