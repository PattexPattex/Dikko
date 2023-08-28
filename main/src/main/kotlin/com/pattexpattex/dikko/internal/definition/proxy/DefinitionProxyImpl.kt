package com.pattexpattex.dikko.internal.definition.proxy

import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.api.definition.DefinitionVisitor
import com.pattexpattex.dikko.api.path.Path

internal open class DefinitionProxyImpl<T : Any> internal constructor(
    override val value: T,
    override val path: Path,
    internal val visitor: DefinitionVisitor
) : DefinitionProxy<T>