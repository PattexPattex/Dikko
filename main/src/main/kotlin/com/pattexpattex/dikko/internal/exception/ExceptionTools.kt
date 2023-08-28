package com.pattexpattex.dikko.internal.exception

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.EventHandler
import net.dv8tion.jda.api.events.GenericEvent
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

object ExceptionTools {
    fun Throwable.friendlyMessage() = buildString {
        append("${this@friendlyMessage::class.simpleName}: $message")

        val cause = cause
        if (cause != null) {
            append(" (caused by ${cause::class.simpleName}")
        }

        if (suppressedExceptions.isNotEmpty()) {
            append(if (cause != null) ", " else "(")
            append("suppressed ${suppressedExceptions.size} more exception${if (suppressedExceptions.size > 1) "s" else ""}")
        }

        if (cause != null || suppressedExceptions.isNotEmpty()) {
            append(")")
        }
    }

    internal fun Throwable.wrap(clazz: KClass<*>? = null, callable: KCallable<*>? = null, event: GenericEvent? = null): Throwable {
        val details = ExceptionDetails(clazz, callable, event)

        if (!suppressedExceptions.contains(details)) {
            addSuppressed(details)
        }

        return this
    }

    internal fun details(
        clazz: KClass<*>? = null,
        callable: KCallable<*>? = null,
        event: GenericEvent? = null
    ) = ExceptionDetails(clazz, callable, event)

    internal fun format(clazz: KClass<*>? = null, callable: KCallable<*>? = null): String {
        val eventHandlerAnn = callable?.findAnnotation<EventHandler>()
        val definitionAnn = callable?.findAnnotation<Definition>()

        val annotationString = if (eventHandlerAnn != null) {
            formatAnnotation(eventHandlerAnn, eventHandlerAnn.pathPattern)
        } else if (definitionAnn != null) {
            formatAnnotation(definitionAnn, definitionAnn.path)
        } else {
            ""
        }

        return annotationString + format(clazz) + format(callable)
    }

    private fun formatAnnotation(annotation: Annotation, vararg values: Any): String {
        var base = "@${annotation.annotationClass.simpleName}"

        if (values.isNotEmpty()) {
            base += "("

            for (value in values) {
                base += "\"$value\", "
            }

            base = base.dropLast(2) + ") "
        }

        return base
    }

    private fun format(clazz: KClass<*>?): String {
        return clazz?.qualifiedName ?: "<not available>"
    }

    private fun format(callable: KCallable<*>?): String {
        return if (callable?.name == null) {
            ""
        } else {
            "#${callable.name}"
        }
    }
}