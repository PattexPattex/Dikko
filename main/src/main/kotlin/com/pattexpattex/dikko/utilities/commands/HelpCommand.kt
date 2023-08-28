package com.pattexpattex.dikko.utilities.commands

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.api.annotations.SuppressChecks
import com.pattexpattex.dikko.api.definition.types.DikkoSlashCommandData
import com.pattexpattex.dikko.api.definition.types.DikkoSlashGroup
import com.pattexpattex.dikko.api.getDispatcher
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.StringSelectEventWrapper
import com.pattexpattex.dikko.internal.implementation.selectmenu.stringSelect
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventDispatcher
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.slash
import com.pattexpattex.dikko.internal.implementation.slashgroup.GroupEventDispatcher
import com.pattexpattex.dikko.utilities.timeout.TimeoutManager
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.editMessage
import dev.minn.jda.ktx.messages.into
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.AutoCompleteQuery
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.utils.SplitUtil
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

//@Suppress("unused")
object HelpCommand {
    private val timeoutManager = TimeoutManager()

    @Definition("/help")
    val definition get() = slash("help", "List of commands & other useful items.") {
        if (Configuration.useSlashOptions) {
            option<String>("group", "Select a group of commands", required = false, autocomplete = true)
        }
    }

    @EventHandler("/help")
    fun handler(event: SlashEventWrapper, @SuppressChecks groupName: String?) {
        event.deferReply().queue()

        val groups = event.getDispatcher<GroupEventDispatcher>()?.proxies
        val group = groups?.get(groupName)?.value
        val commands = getCommands(event, groupName)

        val embeds = buildAllEmbeds(group, commands)

        val selectMenu = if (Configuration.useSelectionMenu && groups?.isNotEmpty() == true) {
            stringSelect("dikko:help.group-select") {
                for (aGroup in groups.values.take(25).map { it.value }) {
                    addOption(aGroup.name, aGroup.id, aGroup.description, aGroup.emoji)
                }
            }
        } else {
            null
        }

        event.hook.editMessage(embeds = embeds, replace = true).apply {
            selectMenu?.let { setComponents(it.into()) }
        }.queue {
            timeoutManager.create("dikko:help", it, timeout = 10.seconds, runPrevious = true)
        }
    }

    @EventHandler("/help/group")
    fun autocompleteHandler(event: AutocompleteEventWrapper, query: AutoCompleteQuery) {
        event.replyChoices((event.getDispatcher<GroupEventDispatcher>()?.proxies?.values ?: emptyList())
            .take(25)
            .filter { it.value.name.startsWith(query.value) }
            .map { Command.Choice(it.value.name, it.value.id) }
        ).queue()
    }

    @EventHandler("dikko:help.group-select")
    fun selectMenuHandler(event: StringSelectEventWrapper, selected: List<SelectOption>) {
        event.deferEdit().queue()

        val groups = event.getDispatcher<GroupEventDispatcher>()?.proxies
        val group = when(selected[0].value) {
            DikkoSlashGroup.HIDDEN.id -> null
            else -> groups?.get(selected[0].value)?.value
        }

        val commands = group?.commands?.map { it.value }
            ?: event.getDispatcher<SlashEventDispatcher>()?.proxies?.values?.map { it.value }
            ?: emptyList() // what

        val embeds = buildAllEmbeds(group, commands)
        event.hook.editMessage(content = "", embeds = embeds, components = event.component.withDisabled(!Configuration.useSelectionMenu).into()).queue()
        timeoutManager.restart("dikko:help")
    }

    private fun buildAllEmbeds(group: DikkoSlashGroup?, commands: List<DikkoSlashCommandData>): List<MessageEmbed> {
        val descriptions = SplitUtil.split(buildString {
            if (group?.description != null) {
                appendLine(group.description)
                appendLine()
            }

            append(formatCommands(commands))
            append(formatLinks())
        }, 4000, true, SplitUtil.Strategy.NEWLINE, SplitUtil.Strategy.WHITESPACE, SplitUtil.Strategy.ANYWHERE)

        val firstEmbed = Embed {
            color = Configuration.color
            title = if (group != null) {
                "${group.emoji?.formatted ?: ""} ${group.name}".trim()
            } else {
                "Help"
            }

            description = descriptions[0]

            if (descriptions.size == 1) {
                footer {
                    timestamp = Instant.now()
                    name = "Powered by Dikko."
                }
            }
        }

        val embeds = arrayListOf(firstEmbed)
        for (i in 1..descriptions.lastIndex) {
            embeds.add(Embed {
                color = Configuration.color
                descriptions[i]

                if (i == descriptions.lastIndex) {
                    footer {
                        timestamp = Instant.now()
                        name = "Powered by Dikko."
                    }
                }
            })
        }

        return embeds
    }

    private fun formatCommands(commands: List<SlashCommandData>): String = if (commands.isNotEmpty()) {
        buildString {
            for (command in commands) {
                appendLine("**/${command.name}**")
                appendLine("  ╰ ${command.description}")
            }
        }
    } else {
        "_Nothing here._"
    }

    private fun getCommands(event: SlashEventWrapper, groupName: String?): List<DikkoSlashCommandData> {
        if (groupName == DikkoSlashGroup.HIDDEN.id) {
            return emptyList()
        }

        val groups = event.getDispatcher<GroupEventDispatcher>()?.proxies?.values?.map { it.value } ?: emptyList()
        val group = groups.find { it.id == groupName }

        return group?.commands?.map { it.value }
            ?: event.getDispatcher<SlashEventDispatcher>()
                ?.proxies
                ?.map { it.value.value }
                ?.filterNot { it.groupId == DikkoSlashGroup.HIDDEN.id }
            ?: emptyList()
    }

    private fun formatLinks(): String = if (Configuration.links.isNotEmpty()) {
        buildString {
            appendLine()

            //Configuration.links["Built by Dikko"] = "https://github.com/PattexPattex/Dikko"

            for ((text, link) in Configuration.links) {
                appendLine("[$text]($link) **|** ")
            }

            removeSuffix(" **|** ")
        }
    } else {
        ""
    }

    object Configuration {
        var color = 0x2C2F33
        var useSlashOptions = true
        var useSelectionMenu = true
        val links = mutableMapOf<String, String>()
    }

    fun configure(block: Configuration.() -> Unit) {
        Configuration.apply(block)
    }
}