package com.pattexpattex.dikko.api.path

interface PathMatcher<T : Path> {
    fun matches(path: Path): Boolean
    fun matcher(path: Path): MatchResult
    fun regex(): Regex

    val parameters: List<PathParameter<T>>
}