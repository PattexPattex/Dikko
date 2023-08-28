package com.pattexpattex.dikko.api.annotations

import com.pattexpattex.dikko.api.event.handler.EventHandlerInstanceFactory
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class UseFactory(val type: KClass<out EventHandlerInstanceFactory>)
