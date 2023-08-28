package com.pattexpattex.dikko.internal.implementation.contextmenu

import com.pattexpattex.dikko.api.definition.types.DikkoUserContextMenuCommandData
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.internal.interactions.CommandDataImpl

internal class DikkoUserContextMenuCommandDataImpl(name: String) : DikkoUserContextMenuCommandData, CommandDataImpl(Command.Type.USER, name) {
    override fun getType() = Command.Type.USER
}