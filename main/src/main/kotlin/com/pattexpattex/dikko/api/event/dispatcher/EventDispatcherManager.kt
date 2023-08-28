package com.pattexpattex.dikko.api.event.dispatcher

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.internal.event.dispatcher.EventDispatcherManagerImpl
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventDispatcher
import com.pattexpattex.dikko.internal.implementation.contextmenu.ContextMenuEventDispatcher
import com.pattexpattex.dikko.internal.implementation.modal.ModalEventDispatcher
import com.pattexpattex.dikko.internal.implementation.selectmenu.SelectEventDispatcher
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventDispatcher
import com.pattexpattex.dikko.internal.implementation.slashgroup.GroupEventDispatcher
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import kotlin.reflect.KClass

interface EventDispatcherManager {
    val dikko: Dikko
    val dispatchers: Dispatchers
    val ctx: CTX

    class Dispatchers internal constructor(private val eventDispatcherManager: EventDispatcherManagerImpl) {
        inline fun <reified T : EventDispatcher<*>> get(): T? = get(T::class)

        @Suppress("unchecked_cast")
        operator fun <T : EventDispatcher<*>> get(dispatcherType: KClass<T>): T? = eventDispatcherManager
            .dispatchersMap
            .values
            .find(dispatcherType::isInstance) as T?

        fun asMap(): Map<KClass<out EventWrapper>, EventDispatcher<*>> = eventDispatcherManager.dispatchersMap

        val autocomplete get() = get<AutocompleteEventDispatcher>()
        val button get() = get<EventDispatcher<Button>>()
        val slash get() = get<SlashEventDispatcher>()
        val slashGroup get() = get<GroupEventDispatcher>()
        val modal get() = get<ModalEventDispatcher>()
        val entitySelect get() = get<SelectEventDispatcher<EntitySelectMenu>>()
        val stringSelect get() = get<SelectEventDispatcher<StringSelectMenu>>()
        val messageMenu get() = get<ContextMenuEventDispatcher>()
        val userMenu get() = get<ContextMenuEventDispatcher>()
    }

    class CTX internal constructor(private val eventDispatcherManager: EventDispatcherManagerImpl) {
        operator fun get(guild: Guild?): GuildContext = eventDispatcherManager.getGuildContext(guild)
        fun asMap(): Map<Long, GuildContext> = eventDispatcherManager.guildContexts
    }
}