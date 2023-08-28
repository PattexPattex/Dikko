package com.pattexpattex.dikko.internal.exception

import com.pattexpattex.dikko.internal.EventPropertyExtractor
import net.dv8tion.jda.api.events.GenericEvent
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

internal class ExceptionDetails(clazz: KClass<*>? = null, callable: KCallable<*>? = null, event: GenericEvent? = null) : RuntimeException() {
    init {
        stackTrace = emptyArray()
    }

    override val message = generateMessage(clazz, callable, event)

    private fun generateMessage(clazz: KClass<*>? = null, callable: KCallable<*>? = null, event: GenericEvent? = null): String {
        var out = ""

        if (clazz != null || callable != null) {
           out += "Member: ${ExceptionTools.format(clazz, callable)}"
        }
        event?.let {
            out += "\nEvent: $it"
        }
        EventPropertyExtractor.getGuild(event)?.let {
            out += "\nGuild: $it"
        }
        EventPropertyExtractor.getUser(event)?.let {
            out += "\n$it"
        }

        return out
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExceptionDetails

        return message == other.message
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}