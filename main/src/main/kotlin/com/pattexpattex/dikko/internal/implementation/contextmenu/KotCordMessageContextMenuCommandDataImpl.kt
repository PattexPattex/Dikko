package com.pattexpattex.dikko.internal.implementation.contextmenu

import com.pattexpattex.dikko.api.definition.types.DikkoMessageContextMenuCommandData
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.internal.interactions.CommandDataImpl

internal class DikkoMessageContextMenuCommandDataImpl(name: String) : DikkoMessageContextMenuCommandData, CommandDataImpl(Command.Type.MESSAGE, name) {
    override fun getType() = Command.Type.MESSAGE
}