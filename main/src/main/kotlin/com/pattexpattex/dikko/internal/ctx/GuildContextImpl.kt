package com.pattexpattex.dikko.internal.ctx

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.internal.event.handler.instances.EventHandlerInstanceFactoryFactory
import net.dv8tion.jda.internal.utils.EntityString
import kotlin.reflect.KClass

internal abstract class GuildContextImpl(override val dikko: Dikko) : GuildContext {
    val id: Long get() = guild?.idLong ?: -1
    private val eventHandlerInstances: MutableMap<KClass<*>, Any> = hashMapOf()

    fun getHandlerInstance(clazz: KClass<*>): Any {
        return eventHandlerInstances.getOrPut(clazz) {
            EventHandlerInstanceFactoryFactory
                .createFactory(clazz)
                .createInstance(dikko)
        }
    }

    override fun toString(): String {
        return EntityString(this).addMetadata("id", id).toString()
    }
}