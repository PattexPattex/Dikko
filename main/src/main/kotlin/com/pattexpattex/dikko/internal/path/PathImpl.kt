package com.pattexpattex.dikko.internal.path

import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.api.path.PathMatcher
import com.pattexpattex.dikko.api.path.PathParameter
import net.dv8tion.jda.internal.utils.EntityString

@PublishedApi
internal open class PathImpl(
    override val value: String,
    override val matcher: PathMatcher<out PathImpl>,
    override val parameters: Map<String, PathParameter<out PathImpl>>
) : Path {
    override fun toString(): String {
        return EntityString(this).addMetadata("value", value).toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathImpl

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    fun fillSegments(matcher: PathMatcher<*>): Path {
        matcher.matcher(this)
        return PathFactory.create(PathFactory.getPathEventType(this), value, matcher)
    }
}