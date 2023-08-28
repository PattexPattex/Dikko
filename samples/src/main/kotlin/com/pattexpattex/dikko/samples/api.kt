package com.pattexpattex.dikko.samples

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.slash
import com.pattexpattex.dikko.utilities.commands.HelpCommand
import dev.minn.jda.ktx.jdabuilder.default
import dev.minn.jda.ktx.jdabuilder.intents
import net.dv8tion.jda.api.requests.GatewayIntent

fun create() {
    val jda = default(System.getenv("token"), enableCoroutines = true) {
        intents += GatewayIntent.MESSAGE_CONTENT
    }

    Dikko.create(jda, "com.example.bot.commands") {
        packages += "com.example.bot.another.package"

        additionalEventHandlers += HelpCommand::class

        failureMessages {
            unknownError = { "Something went wrong 😢" }
        }
    }
}

class SampleHandler { // Must be in a class or an object
    @EventHandler("help")
    fun handler(event: SlashEventWrapper) { // Will handle slash events
        event.reply("no").queue()
    }
}

class SampleDefinition { // Must be in a class or an object
    @Definition("help")
    val definition = slash("help", "Does not help")
}