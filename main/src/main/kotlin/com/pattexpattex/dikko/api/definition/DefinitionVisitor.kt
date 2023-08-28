package com.pattexpattex.dikko.api.definition

import com.pattexpattex.dikko.api.DikkoCallable
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.path.Path
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

internal interface DefinitionVisitor : DikkoCallable<DefinitionProxy<*>> {
    val clazz: KClass<*>
    val callable: KCallable<Any>
    val eventType: KClass<out EventWrapper>
    val path: Path
}