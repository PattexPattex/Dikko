package com.pattexpattex.dikko.internal.implementation.selectmenu

import dev.minn.jda.ktx.interactions.components.EntitySelectMenu
import dev.minn.jda.ktx.interactions.components.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

fun entitySelect(
    customId: String,
    types: Collection<EntitySelectMenu.SelectTarget>,
    placeholder: String? = null,
    valueRange: IntRange = 1..1,
    disabled: Boolean = false,
    builder: EntitySelectMenu.Builder.() -> Unit = {}
) = EntitySelectMenu(customId, types, placeholder, valueRange, disabled, builder)


fun stringSelect(
    customId: String,
    placeholder: String? = null,
    valueRange: IntRange = 1..1,
    disabled: Boolean = false,
    options: Collection<SelectOption> = emptyList(),
    builder: StringSelectMenu.Builder.() -> Unit = {}
) = StringSelectMenu(customId, placeholder, valueRange, disabled, options, builder)