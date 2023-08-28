package com.pattexpattex.dikko.testbot

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.api.annotations.Ignore
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventWrapper
import com.pattexpattex.dikko.internal.implementation.modal.modal
import net.dv8tion.jda.api.interactions.modals.ModalMapping
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

@Ignore
class Modals {
    @Definition("big")
    val big = modal("big", "da") {
        short("a", "a si gej", true)
        paragraph("b", "zakaj", true)
        short("c", "a mas rad punce", false)
    }

    @Definition("foofar.barbaz")
    val foofar = modal("foofar.barbaz", "adada") {
        short("cc", "ne", false)
    }

    @EventHandler("foofar.barbaz")
    fun foofar(event: ModalEventWrapper) = event.reply(event.values[0].asString + "... lol bozo")

    @EventHandler("big")
    fun biggerModal(event: ModalEventWrapper, a: ModalMapping, b: ModalMapping, c: ModalMapping): ReplyCallbackAction {
        return event.reply("${a.asString} ${b.asString} ${c.asString}")
    }
}