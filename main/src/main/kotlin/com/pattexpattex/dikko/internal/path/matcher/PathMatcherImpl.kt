package com.pattexpattex.dikko.internal.path.matcher

import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.api.path.PathMatcher
import com.pattexpattex.dikko.api.path.PathParameter
import com.pattexpattex.dikko.internal.path.PathFactory
import net.dv8tion.jda.internal.utils.EntityString
import kotlin.reflect.KClass

internal open class PathMatcherImpl<T : Path>(
    val pattern: String,
    override val parameters: List<PathParameter<T>>,
    private val pathType: KClass<T>
) : PathMatcher<T> {
    override fun matches(path: Path): Boolean {
        if (path.javaClass != pathType.java) {
            return false
        }

        return regex().matches(path.value)
    }

    override fun matcher(path: Path): MatchResult {
        if (path.javaClass != pathType.java) {
            throw IllegalArgumentException("Incompatible Path type '${path::class.qualifiedName}'")
        }

        return regex().matchEntire(path.value) ?: throw IllegalArgumentException("Path '${path.value}' does not match pattern '$pattern'")
    }

    override fun regex() = Regex(pattern
        .replace(".", "\\.")
        .replace("-", "\\-")
        .replace(PathFactory.Regex.parameter, Regex.escapeReplacement(PathFactory.Regex.valueReplacement))
    )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathMatcherImpl<*>

        return pattern == other.pattern
    }

    override fun hashCode() = pattern.hashCode()

    override fun toString() = EntityString(this).addMetadata("pattern", pattern).toString()
}