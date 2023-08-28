package com.pattexpattex.dikko.internal.implementation.modal

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.minn.jda.ktx.interactions.components.Modal
import net.dv8tion.jda.api.interactions.components.LayoutComponent

fun modal(
    id: String,
    title: String,
    components: Collection<LayoutComponent> = emptyList(),
    builder: InlineModal.() -> Unit
) = Modal(id, title, components, builder)