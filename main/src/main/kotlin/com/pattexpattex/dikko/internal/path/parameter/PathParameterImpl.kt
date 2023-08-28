package com.pattexpattex.dikko.internal.path.parameter

import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.api.path.PathParameter

internal open class PathParameterImpl<T : Path>(
    override val value: String,
    override val index: Int
) : PathParameter<T> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathParameterImpl<*>

        if (value != other.value) return false
        return index == other.index
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + index
        return result
    }
}