package com.pattexpattex.dikko.api.path

interface Path {
    val value: String
    val parameters: Map<String, PathParameter<out Path>>
    val matcher: PathMatcher<out Path>
}
