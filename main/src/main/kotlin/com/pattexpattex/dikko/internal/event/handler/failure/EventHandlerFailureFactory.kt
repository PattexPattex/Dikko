package com.pattexpattex.dikko.internal.event.handler.failure

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.internal.DikkoImpl
import net.dv8tion.jda.api.Permission

object EventHandlerFailureFactory {
    fun create(dikko: Dikko) = EventHandlerFailure(dikko.messageSupplier.unknownError())
    fun eventHandler(dikko: Dikko, path: Path) = EventHandlerFailure(dikko.messageSupplier.noEventHandler(path))
    fun definition(dikko: Dikko, path: Path) = EventHandlerFailure(dikko.messageSupplier.noDefinition(path))
    fun selfPerms(dikko: Dikko, list: List<Permission>) = EventHandlerFailure(dikko.messageSupplier.noSelfPermissions(list))
    fun userPerms(dikko: Dikko, list: List<Permission>) = EventHandlerFailure(dikko.messageSupplier.noUserPermissions(list))
    fun unavailable(dikko: Dikko, guild: Boolean, dm: Boolean) = EventHandlerFailure(dikko.messageSupplier.actionUnavailable(guild, dm))
    fun exception(dikko: Dikko, e: Throwable?) = EventHandlerFailure(when (e) {
        null -> dikko.messageSupplier.unknownError()
        else -> dikko.messageSupplier.invocationException(e)
    })

    private val Dikko.messageSupplier get() = (this as DikkoImpl).eventHandlerFailureMessages
}