package com.pattexpattex.dikko.internal.implementation.slashgroup

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.definition.types.DikkoSlashGroup
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.dispatcher.EventDispatcherImpl

class GroupEventDispatcher internal constructor(dikko: Dikko) : EventDispatcherImpl<DikkoSlashGroup>(dikko) {
    override suspend fun dispatch(event: EventWrapper) { throw UnsupportedOperationException() }
    override fun registerEventHandler(proxy: EventHandlerProxy) {}
}