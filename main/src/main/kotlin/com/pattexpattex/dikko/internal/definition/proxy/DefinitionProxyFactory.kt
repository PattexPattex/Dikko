package com.pattexpattex.dikko.internal.definition.proxy

import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.api.definition.DefinitionVisitor
import com.pattexpattex.dikko.api.definition.types.DikkoMessageContextMenuCommandData
import com.pattexpattex.dikko.api.definition.types.DikkoSlashCommandData
import com.pattexpattex.dikko.api.definition.types.DikkoUserContextMenuCommandData
import com.pattexpattex.dikko.api.path.Path
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.modals.Modal

internal object DefinitionProxyFactory {
    @Suppress("unchecked_cast")
    fun <T : Any> create(value: T, path: Path, visitor: DefinitionVisitor): DefinitionProxy<T> {
        return when (value) {
            is DikkoSlashCommandData -> DefinitionProxyImpl<DikkoSlashCommandData>(value, path, visitor)
            is Button -> DefinitionProxyImpl<Button>(value, path, visitor)
            is Modal -> DefinitionProxyImpl<Modal>(value, path, visitor)
            is StringSelectMenu -> DefinitionProxyImpl<StringSelectMenu>(value, path, visitor)
            is EntitySelectMenu -> DefinitionProxyImpl<EntitySelectMenu>(value, path, visitor)
            is DikkoMessageContextMenuCommandData -> DefinitionProxyImpl<DikkoMessageContextMenuCommandData>(value, path, visitor)
            is DikkoUserContextMenuCommandData -> DefinitionProxyImpl<DikkoUserContextMenuCommandData>(value, path, visitor)
            else -> DefinitionProxyImpl<Any>(value, path, visitor)
        } as DefinitionProxy<T>
    }
}