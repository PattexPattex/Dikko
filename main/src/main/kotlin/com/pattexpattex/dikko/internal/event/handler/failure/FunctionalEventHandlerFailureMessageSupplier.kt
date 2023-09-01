package com.pattexpattex.dikko.internal.event.handler.failure

import com.pattexpattex.dikko.api.event.handler.EventHandlerFailureMessageSupplier
import com.pattexpattex.dikko.api.path.Path
import net.dv8tion.jda.api.Permission

class FunctionalEventHandlerFailureMessageSupplier : EventHandlerFailureMessageSupplier {
    val default = DefaultEventHandlerFailureMessageSupplier()

    var unknownError: () -> String = { default.unknownError() }
    var noEventHandler: (Path) -> String = { default.noEventHandler(it) }
    var noDefinition: (Path) -> String = { default.noDefinition(it) }
    var noSelfPermissions: (List<Permission>) -> String = { default.noSelfPermissions(it) }
    var noUserPermissions: (List<Permission>) -> String = { default.noUserPermissions(it) }
    var actionUnavailable: (availableInGuild: Boolean, availableInDM: Boolean) -> String = { guild, dm ->  default.actionUnavailable(guild, dm) }
    var invocationException: (Throwable) -> String = { default.invocationException(it) }

    override fun unknownError() = unknownError.invoke()
    override fun noEventHandler(path: Path) = noEventHandler.invoke(path)
    override fun noDefinition(path: Path) = noDefinition.invoke(path)
    override fun noSelfPermissions(missingPermissions: List<Permission>) = noSelfPermissions.invoke(missingPermissions)
    override fun noUserPermissions(missingPermissions: List<Permission>) = noUserPermissions.invoke(missingPermissions)
    override fun actionUnavailable(availableInGuild: Boolean, availableInDM: Boolean) = actionUnavailable.invoke(availableInGuild, availableInDM)
    override fun invocationException(exception: Throwable) = invocationException.invoke(exception)
}