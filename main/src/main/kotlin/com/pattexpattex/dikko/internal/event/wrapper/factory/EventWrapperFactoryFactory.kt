package com.pattexpattex.dikko.internal.event.wrapper.factory

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventWrapper
import com.pattexpattex.dikko.internal.implementation.button.ButtonEventWrapper
import com.pattexpattex.dikko.internal.implementation.button.ButtonEventWrapperFactory
import com.pattexpattex.dikko.internal.implementation.contextmenu.MessageContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.contextmenu.UserContextMenuEventWrapper
import com.pattexpattex.dikko.internal.implementation.generic.GenericEventWrapper
import com.pattexpattex.dikko.internal.implementation.generic.GenericEventWrapperFactory
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventWrapper
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventWrapperFactory
import com.pattexpattex.dikko.internal.implementation.selectmenu.EntitySelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.StringSelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import kotlin.reflect.KClass

@PublishedApi
internal object EventWrapperFactoryFactory {
    internal fun createFactory(event: GenericEvent): EventWrapperFactory {
        return when (event) {
            is SlashCommandInteractionEvent -> SlashEventWrapper.Factory()
            is CommandAutoCompleteInteractionEvent -> AutocompleteEventWrapper.Factory()
            is ButtonInteractionEvent -> ButtonEventWrapperFactory()
            is ModalInteractionEvent -> ModalEventWrapperFactory()
            is StringSelectInteractionEvent -> StringSelectEventWrapper.Factory()
            is EntitySelectInteractionEvent -> EntitySelectEventWrapper.Factory()
            is MessageContextInteractionEvent -> MessageContextMenuEventWrapper.Factory()
            is UserContextInteractionEvent -> UserContextMenuEventWrapper.Factory()
            else -> GenericEventWrapperFactory()
        }
    }

    internal fun getWrapperClassForEvent(event: GenericEvent): KClass<out EventWrapper> {
        return when (event) {
            is SlashCommandInteractionEvent -> SlashEventWrapper::class
            is CommandAutoCompleteInteractionEvent -> AutocompleteEventWrapper::class
            is ButtonInteractionEvent -> ButtonEventWrapper::class
            is ModalInteractionEvent -> ModalEventWrapper::class
            is StringSelectInteractionEvent -> StringSelectEventWrapper::class
            is EntitySelectInteractionEvent -> EntitySelectEventWrapper::class
            is MessageContextInteractionEvent -> MessageContextMenuEventWrapper::class
            is UserContextInteractionEvent -> UserContextMenuEventWrapper::class
            else -> GenericEventWrapper::class
        }
    }

    inline fun <reified T : GenericEvent> getWrapperClassForEventType(): KClass<out EventWrapper> {
        return when (T::class) {
            SlashCommandInteractionEvent::class -> SlashEventWrapper::class
            CommandAutoCompleteInteractionEvent::class -> AutocompleteEventWrapper::class
            ButtonInteractionEvent::class -> ButtonEventWrapper::class
            ModalInteractionEvent::class -> ModalEventWrapper::class
            StringSelectInteractionEvent::class -> StringSelectEventWrapper::class
            EntitySelectInteractionEvent::class -> EntitySelectEventWrapper::class
            MessageContextInteractionEvent::class -> MessageContextMenuEventWrapper::class
            UserContextInteractionEvent::class -> UserContextMenuEventWrapper::class
            else -> GenericEventWrapper::class
        }
    }
}