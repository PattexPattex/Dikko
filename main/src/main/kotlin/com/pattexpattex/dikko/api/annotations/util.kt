package com.pattexpattex.dikko.api.annotations

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

internal inline fun <reified T : Annotation> findAnnotation(callable: KCallable<*>, clazz: KClass<*>): T? =
    callable.findAnnotation() ?: clazz.findAnnotation()

internal inline fun <reified T : Annotation> hasAnnotation(callable: KCallable<*>, clazz: KClass<*>): Boolean {
    return if (!callable.hasAnnotation<T>()) {
        clazz.hasAnnotation<T>()
    } else {
        true
    }
}

internal fun canSkipNonCriticalChecks(vararg elements: KAnnotatedElement) =
    elements.any { it.hasAnnotation<SuppressChecks>() }