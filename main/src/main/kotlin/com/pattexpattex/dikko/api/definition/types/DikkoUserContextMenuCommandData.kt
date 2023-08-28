package com.pattexpattex.dikko.api.definition.types

import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface DikkoUserContextMenuCommandData : CommandData {
    override fun getType() = Command.Type.USER
}