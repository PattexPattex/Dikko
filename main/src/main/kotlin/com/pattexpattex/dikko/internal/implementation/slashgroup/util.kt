package com.pattexpattex.dikko.internal.implementation.slashgroup

import com.pattexpattex.dikko.api.definition.types.DikkoSlashGroup
import net.dv8tion.jda.api.entities.emoji.Emoji

fun group(
    id: String,
    name: String = id,
    emoji: Emoji? = null,
    description: String? = null,
    isHidden: Boolean = false
): DikkoSlashGroup = DikkoSlashGroupImpl(id, name, emoji, description, isHidden)