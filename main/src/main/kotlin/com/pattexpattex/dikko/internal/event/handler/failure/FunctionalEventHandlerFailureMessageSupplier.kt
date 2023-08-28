package com.pattexpattex.dikko.internal.event.handler.failure

import com.pattexpattex.dikko.api.path.Path
import net.dv8tion.jda.api.Permission

class FunctionalEventHandlerFailureMessageSupplier : DefaultEventHandlerFailureMessageSupplier() {
    var unknownError: () -> String = { super.unknownError() }
    var noEventHandler: (Path) -> String = { super.noEventHandler(it) }
    var noDefinition: (Path) -> String = { super.noDefinition(it) }
    var noSelfPermissions: (List<Permission>) -> String = { super.noSelfPermissions(it) }
    var noUserPermissions: (List<Permission>) -> String = { super.noUserPermissions(it) }
    var actionUnavailable: (availableInGuild: Boolean, availableInDM: Boolean) -> String = { guild, dm ->  super.actionUnavailable(guild, dm) }
    var invocationException: (Throwable) -> String = { super.invocationException(it) }

    override fun unknownError() = unknownError.invoke()
    override fun noEventHandler(path: Path) = noEventHandler.invoke(path)
    override fun noDefinition(path: Path) = noDefinition.invoke(path)
    override fun noSelfPermissions(missingPermissions: List<Permission>) = noSelfPermissions.invoke(missingPermissions)
    override fun noUserPermissions(missingPermissions: List<Permission>) = noUserPermissions.invoke(missingPermissions)
    override fun actionUnavailable(availableInGuild: Boolean, availableInDM: Boolean) = actionUnavailable.invoke(availableInGuild, availableInDM)
    override fun invocationException(exception: Throwable) = invocationException.invoke(exception)
}