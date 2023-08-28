package com.pattexpattex.dikko.internal.ctx

import com.pattexpattex.dikko.api.Dikko
import dev.minn.jda.ktx.util.ref
import net.dv8tion.jda.api.entities.Guild

internal class DefaultGuildContext(guild: Guild, dikko: Dikko) : GuildContextImpl(dikko) {
    override val guild by guild.ref()
}