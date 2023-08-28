package com.pattexpattex.dikko.internal

import com.pattexpattex.dikko.api.DikkoCallable
import com.pattexpattex.dikko.internal.exception.ReflectCallException
import dev.minn.jda.ktx.util.SLF4J
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion
import net.dv8tion.jda.api.events.GenericEvent
import java.lang.reflect.Method
import kotlin.reflect.KParameter

@PublishedApi
internal object EventPropertyExtractor {
    private val log by SLF4J

    fun getComponentId(event: GenericEvent?): String? = getProperty(event, "getComponentId", "getFullCommandName", "getModalId")

    fun getGuild(event: GenericEvent?): Guild? = getProperty(event, "getGuild")

    fun getUser(event: GenericEvent?): User? = getProperty(event, "getUser", "getAuthor")

    fun getMember(event: GenericEvent?): Member? = getProperty(event, "getMember")

    fun getGuildChannel(event: GenericEvent?): GuildMessageChannelUnion? = getProperty(event, "getGuildChannel", "getChannel")

    private inline fun <reified T : Any> getProperty(event: GenericEvent?, vararg names: String): T? {
        return if (event != null) {
            event::class.java.methods.find { it.name in names && it.returnType == T::class.java }?.let {
                Caller(it, event).callBlocking()
            }?.getOrElse {
                log.warn("Calling property getter(s) ${names.contentToString()} on event $event threw an exception", it)
                null
            }
        } else {
            null
        } as T?
    }

    private class Caller(private val method: Method, private val event: GenericEvent) : DikkoCallable<Any?> {
        override fun callBlocking(args: Map<KParameter, Any?>): Result<Any?> {
            return try {
                method.isAccessible = true
                val out = method.invoke(event, *args.values.toTypedArray())
                Result.success(out)
            } catch (e: Throwable) {
                Result.failure(ReflectCallException("An exception occurred when calling property getter", e))
            }
        }
    }
}