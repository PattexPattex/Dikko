package com.pattexpattex.dikko.api.definition.types

import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.internal.implementation.slashgroup.DikkoSlashGroupImpl
import dev.minn.jda.ktx.emoji.toEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji

interface DikkoSlashGroup {
    val id: String
    val name: String
    val emoji: Emoji?
    val description: String?
    val isHidden: Boolean
    val commands: List<DefinitionProxy<DikkoSlashCommandData>>

    companion object {
        val UNGROUPED: DikkoSlashGroup = DikkoSlashGroupImpl(
            "ungrouped",
            "Ungrouped",
            "\uD83D\uDDC2️".toEmoji(),
            "Ungrouped commands.",
            false
        )
        val HIDDEN: DikkoSlashGroup = DikkoSlashGroupImpl(
            "hidden",
            isHidden = true
        )
    }
}