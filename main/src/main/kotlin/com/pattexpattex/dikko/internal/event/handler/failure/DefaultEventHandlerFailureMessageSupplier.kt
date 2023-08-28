package com.pattexpattex.dikko.internal.event.handler.failure

import com.pattexpattex.dikko.api.event.handler.EventHandlerFailureMessageSupplier
import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.internal.exception.ExceptionTools.friendlyMessage
import net.dv8tion.jda.api.Permission

open class DefaultEventHandlerFailureMessageSupplier : EventHandlerFailureMessageSupplier {
    override fun unknownError() = "Something went wrong."
    override fun noEventHandler(path: Path) = "This action is not supported."
    override fun noDefinition(path: Path) = "Missing action definition."
    override fun noSelfPermissions(missingPermissions: List<Permission>) = "I do not have sufficient permissions to perform this action."
    override fun noUserPermissions(missingPermissions: List<Permission>) = "You do not have sufficient permissions to perform this action."
    override fun actionUnavailable(availableInGuild: Boolean, availableInDM: Boolean) = when {
        availableInGuild -> "Sorry, this action is only available in servers."
        availableInDM -> "Sorry, this action is only available in direct messages."
        else -> "Sorry, this action is not available."
    }
    override fun invocationException(exception: Throwable) = "Something went wrong: `${exception.friendlyMessage()}`"
}