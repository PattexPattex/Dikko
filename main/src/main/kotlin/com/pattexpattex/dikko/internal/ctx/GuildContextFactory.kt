package com.pattexpattex.dikko.internal.ctx

import com.pattexpattex.dikko.api.Dikko
import net.dv8tion.jda.api.entities.Guild

internal object GuildContextFactory {
    fun create(guild: Guild?, dikko: Dikko): GuildContextImpl {
        return when (guild) {
            null -> PrivateGuildContext(dikko)
            else -> DefaultGuildContext(guild, dikko)
        }
    }
}