package com.pattexpattex.dikko.api.event.dispatcher

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.definition.DefinitionProxy

interface EventDispatcher<T : Any> {
    val proxies: Map<String, DefinitionProxy<T>>
    val dikko: Dikko
}