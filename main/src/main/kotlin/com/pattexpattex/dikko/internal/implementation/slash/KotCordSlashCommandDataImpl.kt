package com.pattexpattex.dikko.internal.implementation.slash

import com.pattexpattex.dikko.api.definition.types.DikkoSlashCommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

internal class DikkoSlashCommandDataImpl(
    name: String,
    description: String,
    override val groupId: String?
) : DikkoSlashCommandData, CommandDataImpl(name, description)