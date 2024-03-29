package com.pattexpattex.dikko.testbot

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import com.pattexpattex.dikko.api.annotations.Ignore
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.slash
import com.pattexpattex.dikko.utilities.pagination.pagination
import com.pattexpattex.dikko.utilities.prompt.prompt
import dev.minn.jda.ktx.emoji.toEmoji
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.MessageEdit
import dev.minn.jda.ktx.messages.into
import dev.minn.jda.ktx.messages.send
import kotlinx.coroutines.coroutineScope
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.util.function.Supplier

@Ignore(Slashes.Companion::class)
class Slashes {
    @Definition("foo") val foo = slash("foo", "foo far bar baz")
    @Definition("far") val far = slash("far", "foo far bar baz")

    companion object : Supplier<Boolean> {
        override fun get(): Boolean {
            println("got called")
            return true
        }
    }

    @EventHandler("foo")
    suspend fun foo(event: SlashEventWrapper) = coroutineScope {
        event.guild!!.name

        val pagination = pagination {
            pages {
                + MessageEdit("foo", components = button("dan", "adan", disabled = true).into())
                + MessageEdit("off", components = button("noc", "onoc", disabled = true).into())
                + MessageEdit("srg", components = button("srg", "srg", disabled = true).into())
            }

            filter {
                it.member?.hasPermission(Permission.ADMINISTRATOR) ?: true
            }
        }.build(event)

        val result = pagination.result.await()
        println(result.timeCreated)
    }

    @Ignore
    @EventHandler("far")
    suspend fun far(event: SlashEventWrapper) = coroutineScope {
        val prompt = prompt("yes?") {
            filter {
                val currentChannel = event.guild!!.audioManager.connectedChannel!!
                it.member in currentChannel.members
            }

            options {
                option("yes", "yes", ButtonStyle.SUCCESS, "✅".toEmoji())
                cancel()
            }
        }.build(event)

        prompt.timeout()

        for (result in prompt.channel) {
            result.event?.hook?.send(result.option.text)
        }

        val result = prompt.result.await()
        println(result.option.text)
    }
}
