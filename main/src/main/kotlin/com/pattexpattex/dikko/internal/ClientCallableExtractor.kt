package com.pattexpattex.dikko.internal

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.annotations.Ignore
import com.pattexpattex.dikko.internal.exception.ExceptionTools.wrap
import dev.minn.jda.ktx.util.SLF4J
import io.github.classgraph.ClassGraph
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

internal object ClientCallableExtractor {
    fun findDefinitionCallables(
        packages: List<String>,
        classes: List<String>
    ): List<Pair<KClass<*>, KCallable<Any>>> = ClassGraph()
        .enableAllInfo()
        .acceptPackages(*packages.toTypedArray())
        .acceptClasses(*classes.toTypedArray())
        .scan()
        .use { result ->
            result.getClassesWithMethodAnnotation(Definition::class.java)
                .filterNot { it.hasAnnotation(Ignore::class.java) }
                .flatMap { classInfo ->
                    classInfo.loadClass().kotlin.let { kClass ->
                        kClass.members
                            .filter { it.hasAnnotation<Definition>() }
                            .filterNot { it.hasAnnotation<Ignore>() }
                            .filter { it.returnType.isSubtypeOf(typeOf<Any>()) }
                            .mapNotNull {
                                val cast = tryCastToAny(
                                    kClass,
                                    it
                                ) ?: return@mapNotNull null
                                kClass to cast
                            }
                    }
            }
        }

    inline fun <reified T : Annotation> findAnnotatedCallables(
        packages: List<String>,
        classes: List<String>
    ): List<Pair<KClass<*>, KCallable<*>>> = ClassGraph()
        .enableAllInfo()
        .acceptPackages(*packages.toTypedArray())
        .acceptClasses(*classes.toTypedArray())
        .scan()
        .use { result ->
            result.getClassesWithMethodAnnotation(T::class.java)
                .filterNot { it.hasAnnotation(Ignore::class.java) }
                .flatMap { classInfo ->
                    classInfo.loadClass().kotlin.let { kClass ->
                        kClass.members
                            .filter { it.hasAnnotation<T>() }
                            .filterNot { it.hasAnnotation<Ignore>() }
                            .map { kClass to it }
                    }
            }
        }

    private val log by SLF4J

    @Suppress("unchecked_cast")
    private fun tryCastToAny(clazz: KClass<*>, callable: KCallable<*>): KCallable<Any>? {
        return if (callable.returnType.isSubtypeOf(typeOf<Any>())) {
            callable as KCallable<Any>
        } else {
            log.warn(
                "Member is annotated by @${Definition::class.simpleName} but its return type is Unit",
                ClassCastException().wrap(clazz, callable)
            )
            null
        }
    }
}