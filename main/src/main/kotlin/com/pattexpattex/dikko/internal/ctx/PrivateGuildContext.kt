package com.pattexpattex.dikko.internal.ctx

import com.pattexpattex.dikko.api.Dikko

internal class PrivateGuildContext(dikko: Dikko) : GuildContextImpl(dikko) {
    override val guild = null
}