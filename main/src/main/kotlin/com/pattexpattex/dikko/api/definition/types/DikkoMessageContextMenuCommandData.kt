package com.pattexpattex.dikko.api.definition.types

import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface DikkoMessageContextMenuCommandData : CommandData {
    override fun getType() = Command.Type.MESSAGE
}