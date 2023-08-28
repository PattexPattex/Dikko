package com.pattexpattex.dikko.api.ctx

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.internal.ctx.GuildContextImpl
import net.dv8tion.jda.api.entities.Guild
import kotlin.reflect.KClass

interface GuildContext {
    val dikko: Dikko
    val guild: Guild?

    companion object {
        @Suppress("unchecked_cast")
        fun <T : Any> GuildContext.getHandler(clazz: KClass<T>) = (this as GuildContextImpl).getHandlerInstance(clazz) as T

        inline fun <reified T : Any> GuildContext.getHandler() = getHandler(T::class)
    }
}
