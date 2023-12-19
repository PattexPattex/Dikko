package com.pattexpattex.dikko.internal.event.dispatcher

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.dispatcher.EventDispatcher
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventDispatcher
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventWrapper
import com.pattexpattex.dikko.internal.implementation.button.ButtonEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.ContextMenuEventDispatcher
import com.pattexpattex.dikko.internal.implementation.contextmenu.MessageContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.UserContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventDispatcher
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.EntitySelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.SelectEventDispatcher
import com.pattexpattex.dikko.internal.implementation.selectmenu.StringSelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventDispatcher
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import com.pattexpattex.dikko.internal.implementation.slashgroup.GroupEventDispatcher
import com.pattexpattex.dikko.internal.implementation.slashgroup.GroupEventWrapper
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import kotlin.reflect.KClass

internal object EventDispatcherFactory {
    inline fun <reified T : EventWrapper> create(dikko: Dikko) = create(T::class, dikko)

    fun create(eventType: KClass<out EventWrapper>, dikko: Dikko): EventDispatcher<*> {
        return when (eventType) {
            SlashEventWrapper::class -> SlashEventDispatcher(dikko)
            GroupEventWrapper::class -> GroupEventDispatcher(dikko)
            ButtonEventWrapper::class -> EventDispatcherImpl<Button>(dikko)
            ModalEventWrapper::class -> ModalEventDispatcher(dikko)
            StringSelectEventWrapper::class -> SelectEventDispatcher<StringSelectMenu>(dikko)
            EntitySelectEventWrapper::class -> SelectEventDispatcher<EntitySelectMenu>(dikko)
            MessageContextMenuEventWrapper::class -> ContextMenuEventDispatcher.Message(dikko)
            UserContextMenuEventWrapper::class -> ContextMenuEventDispatcher.User(dikko)
            AutocompleteEventWrapper::class -> AutocompleteEventDispatcher(dikko)
            else -> throw IllegalArgumentException("Unsupported event type '${eventType.qualifiedName}'")
        }
    }
}