package com.pattexpattex.dikko.testbot

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.definition.types.DikkoSlashGroup
import com.pattexpattex.dikko.internal.implementation.slashgroup.group
import dev.minn.jda.ktx.emoji.toEmoji

class Groups {
    @Definition("abeceda")
    val abeceda = group("abeceda", "ABECEDAAAA", "🆎".toEmoji(), "ABCČDEFGHIJKLMNOPRSŠTUVZŽ")

    @Definition("ungrouped")
    val ungrouped = DikkoSlashGroup.UNGROUPED
}