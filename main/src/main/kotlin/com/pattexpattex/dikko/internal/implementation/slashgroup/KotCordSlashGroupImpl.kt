package com.pattexpattex.dikko.internal.implementation.slashgroup

import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.api.definition.types.DikkoSlashCommandData
import com.pattexpattex.dikko.api.definition.types.DikkoSlashGroup
import net.dv8tion.jda.api.entities.emoji.Emoji

class DikkoSlashGroupImpl internal constructor(
    override val id: String,
    override val name: String = id,
    override val emoji: Emoji? = null,
    override val description: String? = null,
    override val isHidden: Boolean = false
) : DikkoSlashGroup {
    override val commands: List<DefinitionProxy<DikkoSlashCommandData>> = arrayListOf()

    internal fun addCommand(proxy: DefinitionProxy<DikkoSlashCommandData>) {
        (commands as ArrayList).add(proxy)
    }
}