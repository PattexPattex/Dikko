package com.pattexpattex.dikko.api.event.handler

import com.pattexpattex.dikko.api.path.Path
import net.dv8tion.jda.api.Permission

interface EventHandlerFailureMessageSupplier {
    fun unknownError(): String
    fun noEventHandler(path: Path): String = unknownError()
    fun noDefinition(path: Path): String = unknownError()
    fun noSelfPermissions(missingPermissions: List<Permission>): String = unknownError()
    fun noUserPermissions(missingPermissions: List<Permission>): String = unknownError()
    fun actionUnavailable(availableInGuild: Boolean, availableInDM: Boolean): String = unknownError()
    fun invocationException(exception: Throwable): String = unknownError()
}