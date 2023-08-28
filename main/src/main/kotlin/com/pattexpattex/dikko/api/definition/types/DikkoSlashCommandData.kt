package com.pattexpattex.dikko.api.definition.types

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

interface DikkoSlashCommandData : SlashCommandData {
    val groupId: String?
}