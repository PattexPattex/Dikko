package com.pattexpattex.dikko.testbot

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.api.annotations.Ignore
import com.pattexpattex.dikko.internal.implementation.contextmenu.MessageContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.UserContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.messageContext
import com.pattexpattex.dikko.internal.implementation.contextmenu.userContext
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message

@Ignore
class ContextMenus {
    @Definition("adad")
    val msg = messageContext("adad")

    @Definition("soief")
    val user = userContext("soief")

    @EventHandler("adad")
    fun msg(event: MessageContextMenuEventWrapper, target: Message) {
        event.reply("look ${target.id}").queue()

    }

    @EventHandler("soief")
    fun user(event: UserContextMenuEventWrapper, target: IMentionable) {
        event.target

        event.reply(target.asMention + "wuz 'ere").queue()
    }
}