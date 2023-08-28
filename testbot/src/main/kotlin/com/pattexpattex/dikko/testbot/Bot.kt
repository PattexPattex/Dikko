package com.pattexpattex.dikko.testbot

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.utilities.commands.HelpCommand
import dev.minn.jda.ktx.jdabuilder.default
import dev.minn.jda.ktx.jdabuilder.intents
import net.dv8tion.jda.api.requests.GatewayIntent

fun main() {
    val jda = default(System.getenv("TOKEN"), enableCoroutines = true) {
        intents += listOf(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
    }

    Dikko.create(jda, "com.pattexpattex.dikko.testbot") {
        additionalEventHandlers += HelpCommand::class
    }
}
