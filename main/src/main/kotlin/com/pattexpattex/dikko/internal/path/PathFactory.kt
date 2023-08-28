package com.pattexpattex.dikko.internal.path

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.api.path.PathMatcher
import com.pattexpattex.dikko.internal.implementation.autocomplete.AutocompleteEventWrapper
import com.pattexpattex.dikko.internal.implementation.button.ButtonEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.SlashEventWrapper
import com.pattexpattex.dikko.internal.implementation.slash.SlashPath
import com.pattexpattex.dikko.internal.path.matcher.PathMatcherFactory
import com.pattexpattex.dikko.internal.path.parameter.PathParameterImpl
import kotlin.reflect.KClass

object PathFactory {
    inline fun <reified T : EventWrapper> create(path: String, pattern: String? = null) = create(T::class, path, pattern ?: path)
    inline fun <reified T : EventWrapper> create(path: String, matcher: PathMatcher<*>?) = create(T::class, path, matcher)

    fun create(eventType: KClass<out EventWrapper>, path: String, pattern: String? = null): Path {
        val matcher = PathMatcherFactory.create(eventType, pattern ?: path)
        return create(eventType, path, matcher)
    }

    fun create(eventType: KClass<out EventWrapper>, path: String, matcher: PathMatcher<*>?): Path {
        if (matcher == null) {
            return create(eventType, path)
        }

        return when (eventType) {
            SlashEventWrapper::class, AutocompleteEventWrapper::class -> createSlash(path, matcher)
            else -> createImpl(path, matcher)
        }
    }

    internal fun getPathEventType(path: Path): KClass<out EventWrapper> {
        return when (path) {
            is SlashPath -> SlashEventWrapper::class
            else -> ButtonEventWrapper::class
        }
    }

    @Suppress("unchecked_cast")
    internal fun createImpl(value: String, matcher: PathMatcher<*>): PathImpl {
        if (!Regex.id.matches(value)) {
            throw IllegalArgumentException("Input is invalid")
        }

        val regex = matcher.regex()
        val match = regex.matchEntire(value) ?: throw IllegalArgumentException("Path '$value' does not match pattern '${regex.pattern}'")

        val parameters = matcher.parameters.associate {
            it.value to PathParameterImpl<PathImpl>(match.groups[it.index + 1]!!.value, it.index)
        }

        return PathImpl(value, matcher as PathMatcher<PathImpl>, parameters)
    }

    @Suppress("unchecked_cast")
    internal fun createSlash(path: String, matcher: PathMatcher<*>): SlashPath {
        if (!Regex.slash.matches(path)) {
            throw IllegalArgumentException("'$path' is not a valid path")
        }

        val segments = path
            .replace("/", " ")
            .trim()
            .replace(" ", "/")
            .split("/")
            .mapIndexed { index, s -> PathParameterImpl<SlashPath>(s, index) }
            .associateBy { it.value }

        return SlashPath("/$path".replace(" ", "/").replace("//", "/"), matcher as PathMatcher<SlashPath>, segments)
    }

    internal object Regex {
        val slash = Regex("^([/ ]?[a-z-]+){1,4}\$")
        val id = Regex("^[a-z\\d_-]+(?:[.:][a-z\\d_-]+)*\$")
        val parameter = Regex("\\{([a-z\\d_-]+?)}")
        const val valueReplacement = "([a-z\\d_-]+)"
        val matcherId = Regex("^(?:[a-z\\d_-]+|\\{[a-z\\d_-]+})(?:[.:](?:[a-z\\d_-]+|\\{[a-z\\d_-]+}))*\$")
    }
}