package com.pattexpattex.dikko.internal.implementation.contextmenu

import com.pattexpattex.dikko.api.definition.types.DikkoMessageContextMenuCommandData
import com.pattexpattex.dikko.api.definition.types.DikkoUserContextMenuCommandData
import net.dv8tion.jda.api.interactions.commands.build.CommandData

fun messageContext(name: String, builder: CommandData.() -> Unit = {}): DikkoMessageContextMenuCommandData =
    DikkoMessageContextMenuCommandDataImpl(name).apply(builder)

fun userContext(name: String, builder: CommandData.() -> Unit = {}): DikkoUserContextMenuCommandData =
    DikkoUserContextMenuCommandDataImpl(name).apply(builder)