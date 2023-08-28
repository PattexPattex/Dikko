package com.pattexpattex.dikko.internal.implementation.slash

import com.pattexpattex.dikko.api.path.PathMatcher
import com.pattexpattex.dikko.api.path.PathParameter
import com.pattexpattex.dikko.internal.path.PathImpl

internal class SlashPath(
    value: String,
    matcher: PathMatcher<SlashPath>,
    segments: Map<String, PathParameter<SlashPath>>
) : PathImpl(value, matcher, segments)