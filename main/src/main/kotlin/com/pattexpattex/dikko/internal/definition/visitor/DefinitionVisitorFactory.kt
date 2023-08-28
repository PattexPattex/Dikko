package com.pattexpattex.dikko.internal.definition.visitor

import com.pattexpattex.dikko.api.definition.types.DikkoMessageContextMenuCommandData
import com.pattexpattex.dikko.api.definition.types.DikkoSlashCommandData
import com.pattexpattex.dikko.api.definition.types.DikkoSlashGroup
import com.pattexpattex.dikko.api.definition.types.DikkoUserContextMenuCommandData
import com.pattexpattex.dikko.api.definition.DefinitionVisitor
import com.pattexpattex.dikko.internal.exception.ExceptionTools.wrap
import com.pattexpattex.dikko.internal.implementation.button.ButtonEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.MessageContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.UserContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.EntitySelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.StringSelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import com.pattexpattex.dikko.internal.implementation.slashgroup.GroupEventWrapper
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.modals.Modal
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

internal object DefinitionVisitorFactory {
    fun create(clazz: KClass<*>, callable: KCallable<Any>): DefinitionVisitor {
        return when (val definitionType = callable.returnType) {
            typeOf<Button>() -> DefinitionVisitorImpl(clazz, callable, ButtonEventWrapper::class)
            typeOf<Modal>() -> DefinitionVisitorImpl(clazz, callable, ModalEventWrapper::class)
            typeOf<DikkoSlashCommandData>() -> DefinitionVisitorImpl(clazz, callable, SlashEventWrapper::class)
            typeOf<DikkoSlashGroup>() -> DefinitionVisitorImpl(clazz, callable, GroupEventWrapper::class)
            typeOf<StringSelectMenu>() -> DefinitionVisitorImpl(clazz, callable, StringSelectEventWrapper::class)
            typeOf<EntitySelectMenu>() -> DefinitionVisitorImpl(clazz, callable, EntitySelectEventWrapper::class)
            typeOf<DikkoMessageContextMenuCommandData>() -> DefinitionVisitorImpl(clazz, callable, MessageContextMenuEventWrapper::class)
            typeOf<DikkoUserContextMenuCommandData>() -> DefinitionVisitorImpl(clazz, callable, UserContextMenuEventWrapper::class)
            else -> throw IllegalArgumentException("Unsupported definition type '$definitionType'").wrap(clazz, callable)
        }
    }
}