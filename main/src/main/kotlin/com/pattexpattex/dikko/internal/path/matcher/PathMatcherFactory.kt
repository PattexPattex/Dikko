package com.pattexpattex.dikko.internal.path.matcher

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.path.PathMatcher
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.SlashPath
import com.pattexpattex.dikko.internal.path.PathFactory
import com.pattexpattex.dikko.internal.path.PathImpl
import com.pattexpattex.dikko.internal.path.parameter.PathParameterImpl
import kotlin.reflect.KClass

object PathMatcherFactory {
    inline fun <reified T : EventWrapper> create(pattern: String) = create(T::class, pattern)

    fun create(eventType: KClass<out EventWrapper>, pattern: String): PathMatcher<*> {
        return when (eventType) {
            SlashEventWrapper::class, AutocompleteEventWrapper::class -> createSlash(pattern)
            else -> createImpl(pattern)
        }
    }

    internal fun createImpl(pattern: String): PathMatcher<PathImpl> {
        if (!PathFactory.Regex.matcherId.matches(pattern)) {
            throw IllegalArgumentException("Input '$pattern' is invalid")
        }

        val parameters = PathFactory.Regex.parameter
            .findAll(pattern)
            .mapIndexedNotNull { i, result ->
                val name = result.groups[1]?.value ?: return@mapIndexedNotNull null
                PathParameterImpl<PathImpl>(name, i)
            }.toList()

        return PathMatcherImpl(pattern, parameters, PathImpl::class)
    }

    internal fun createSlash(pattern: String): PathMatcher<SlashPath> {
        if (!PathFactory.Regex.slash.matches(pattern)) {
            throw IllegalArgumentException("'$pattern' is not a valid path")
        }

        return PathMatcherImpl("/$pattern".replace(" ", "/").replace("//", "/"), emptyList(), SlashPath::class)
    }
}