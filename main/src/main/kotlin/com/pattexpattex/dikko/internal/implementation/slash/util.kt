package com.pattexpattex.dikko.internal.implementation.slash

import com.pattexpattex.dikko.api.definition.types.DikkoSlashCommandData
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

fun slash(
    name: String,
    description: String,
    group: String? = null,
    builder: SlashCommandData.() -> Unit = {},
): DikkoSlashCommandData = DikkoSlashCommandDataImpl(name, description, group).apply(builder)

internal fun SlashCommandData.getSubcommand(path: SlashPath): SubcommandData? {
    val segments = path.parameters.values.toList()

    return when (path.parameters.size) {
        2 -> subcommands.find { it.name == segments[1].value }
        3 -> subcommandGroups.find { it.name == segments[1].value }?.subcommands?.find { it.name == segments[2].value }
        else -> null
    }
}

internal fun OptionType.acceptableClasses() = when (this) {
    OptionType.ATTACHMENT -> listOf(Message.Attachment::class)
    OptionType.BOOLEAN -> listOf(Boolean::class)
    OptionType.CHANNEL -> listOf(Channel::class)
    OptionType.INTEGER -> listOf(Long::class, Int::class, Short::class, Byte::class)
    OptionType.MENTIONABLE -> listOf(IMentionable::class)
    OptionType.NUMBER -> listOf(Double::class, Float::class)
    OptionType.ROLE -> listOf(Role::class)
    OptionType.STRING -> listOf(String::class)
    OptionType.USER -> listOf(User::class, Member::class)
    else -> emptyList()
}

internal fun OptionData.acceptableTypes() = type.acceptableClasses().map { it.createType(nullable = !isRequired) }

internal fun CommandInteractionPayload.getOptionValue(name: String, type: KType): Any? {
    return when (type.classifier) {
        typeOf<Float>().classifier, typeOf<Double>().classifier -> getOption(name, OptionMapping::getAsInt)
        typeOf<Int>().classifier, typeOf<Long>().classifier, typeOf<Short>().classifier, typeOf<Byte>().classifier -> getOption(name, OptionMapping::getAsInt)
        typeOf<String>().classifier -> getOption(name, OptionMapping::getAsString)
        typeOf<Member>().classifier -> getOption(name, OptionMapping::getAsMember)
        typeOf<User>().classifier -> getOption(name, OptionMapping::getAsUser)
        typeOf<Role>().classifier -> getOption(name, OptionMapping::getAsRole)
        typeOf<Boolean>().classifier -> getOption(name, OptionMapping::getAsBoolean)
        typeOf<Message.Attachment>().classifier -> getOption(name, OptionMapping::getAsAttachment)
        else -> when {
            typeOf<GuildChannel>().isSupertypeOf(type) -> getOption(name, OptionMapping::getAsChannel)
            typeOf<IMentionable>().isSupertypeOf(type) -> getOption(name, OptionMapping::getAsMentionable)
            else -> null
        }
    }
}