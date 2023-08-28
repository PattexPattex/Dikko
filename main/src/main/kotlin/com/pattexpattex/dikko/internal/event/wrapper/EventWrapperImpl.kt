package com.pattexpattex.dikko.internal.event.wrapper

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.internal.event.wrapper.factory.EventWrapperFactoryFactory
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.internal.utils.EntityString

abstract class EventWrapperImpl internal constructor(
    protected val event: GenericEvent,
    override val ctx: GuildContext,
    override val dikko: Dikko,
    override val path: Path
) : EventWrapper {
    override fun getResponseNumber() = event.responseNumber
    override fun getRawData() = event.rawData

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventWrapper

        return path == other.path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun toString(): String {
        return EntityString(this)
            .addMetadata("path", path)
            .addMetadata("ctx", ctx)
            .addMetadata("responseNumber", responseNumber)
            .toString()
    }

    internal fun createWithHandlerProxy(proxy: EventHandlerProxy): EventWrapper {
        return EventWrapperFactoryFactory.createFactory(event).create(event, ctx, dikko, proxy)
    }
}