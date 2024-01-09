package com.pattexpattex.dikko.api.annotations

import java.util.function.Supplier
import kotlin.reflect.KClass

/**
 * @param predicate returns true to ignore
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Ignore(val predicate: KClass<out Supplier<Boolean>> = DefaultPredicate::class)

object DefaultPredicate : Supplier<Boolean> {
    override fun get() = true
}
