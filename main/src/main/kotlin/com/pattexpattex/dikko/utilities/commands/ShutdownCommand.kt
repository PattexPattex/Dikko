package com.pattexpattex.dikko.utilities.commands

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.internal.event.handler.failure.EventHandlerFailureFactory
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.slash
import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import kotlin.system.exitProcess

object ShutdownCommand {
    @Definition("/shutdown")
    val definition = slash("shutdown", "Stops this bot.") {
        if (Configuration.ownerId == -1L) {
            defaultPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
        }
    }

    @EventHandler("/shutdown")
    suspend fun handler(event: SlashEventWrapper): Any {
        val ownerId = Configuration.ownerId
        if (ownerId != -1L && event.user.idLong != ownerId) {
            return EventHandlerFailureFactory.userPerms(event.dikko, emptyList())
        }

        event.reply_("Shutting down.", ephemeral = true).await()
        event.dikko.shutdown()
        event.jda.shutdown()
        exitProcess(0)
    }

    object Configuration {
        var ownerId: Long = -1
    }

    fun configure(block: Configuration.() -> Unit) {
        Configuration.apply(block)
    }
}