package com.pattexpattex.dikko.testbot

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.api.annotations.Ignore
import com.pattexpattex.dikko.api.ctx.GuildContext.Companion.getHandler
import com.pattexpattex.dikko.api.getDispatcher
import com.pattexpattex.dikko.internal.event.dispatcher.EventDispatcherImpl
import com.pattexpattex.dikko.internal.implementation.selectmenu.EntitySelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.StringSelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.entitySelect
import com.pattexpattex.dikko.internal.implementation.selectmenu.stringSelect
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import dev.minn.jda.ktx.interactions.components.option
import dev.minn.jda.ktx.messages.into
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.requests.CompletedRestAction

@Ignore
class Selections {
    @Definition("ntt")
    val entity = entitySelect("ntt", listOf(EntitySelectMenu.SelectTarget.USER))

    @Definition("streeng")
    val string = stringSelect("streeng") {
        option("ee", "ee")
        option("ff", "ff")
        option("dd", "dd")
    }

    @EventHandler("ntt")
    fun entity(event: EntitySelectEventWrapper, values: List<IMentionable>) {
        event.reply("foofar ${values.joinToString { it.asMention + " " }}").queue()

        CompletedRestAction(event.jda, event)
    }

    @EventHandler("streeng")
    fun string(event: StringSelectEventWrapper, values: List<SelectOption>) {
        event.reply("yea\n${values.joinToString { "${it.label}: ${it.value}\n" }}").queue()
    }

    fun slash(event: SlashEventWrapper) {
        event.reply_(components = event.ctx
            .getHandler<Selections>()
            .entity
            .into())

        event.getDispatcher<EventDispatcherImpl<Button>>()
            ?.proxies
            ?.get("btn")
            ?.value
    }
}