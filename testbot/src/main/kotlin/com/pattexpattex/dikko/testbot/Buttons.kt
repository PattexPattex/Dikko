package com.pattexpattex.dikko.testbot

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.internal.implementation.button.ButtonEventWrapper
import dev.minn.jda.ktx.emoji.toEmoji
import dev.minn.jda.ktx.interactions.components.button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle

class Buttons {
    @Definition("banana.foo")
    val foo = button("banana.foo", "Yee", "🍌".toEmoji(), ButtonStyle.SECONDARY, false)

    @EventHandler("banana.{type}")
    fun handler(event: ButtonEventWrapper) {
        event.reply(event.path.parameters["type"]!!.value).queue()
    }
}