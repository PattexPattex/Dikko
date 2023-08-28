package com.pattexpattex.dikko.api.definition

import com.pattexpattex.dikko.api.path.Path

interface DefinitionProxy<T : Any> {
    val value: T
    val path: Path
}