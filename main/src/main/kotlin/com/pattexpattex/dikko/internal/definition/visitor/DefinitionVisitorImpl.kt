package com.pattexpattex.dikko.internal.definition.visitor

import com.pattexpattex.dikko.api.annotations.Definition
import com.pattexpattex.dikko.api.definition.DefinitionProxy
import com.pattexpattex.dikko.api.definition.DefinitionVisitor
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.path.Path
import com.pattexpattex.dikko.internal.definition.proxy.DefinitionProxyFactory
import com.pattexpattex.dikko.internal.exception.ExceptionTools.wrap
import com.pattexpattex.dikko.internal.exception.ReflectCallException
import com.pattexpattex.dikko.internal.path.PathFactory
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible

internal open class DefinitionVisitorImpl internal constructor(
    override val clazz: KClass<*>,
    override val callable: KCallable<Any>,
    override val eventType: KClass<out EventWrapper>
) : DefinitionVisitor {
    override val path: Path = getPath()
    private var result: Result<DefinitionProxy<*>>? = null

    override suspend fun call(args: Map<KParameter, Any?>): Result<DefinitionProxy<*>> {
        val resultLocal = result
        if (resultLocal != null && resultLocal.isSuccess) {
            return resultLocal
        }

        result = try {
            callable.isAccessible = true
            val out = callable.callSuspendBy(args)
            Result.success(DefinitionProxyFactory.create(out, path, this))
        } catch (e: Throwable) {
            Result.failure(ReflectCallException("Failed to retrieve definition", e).wrap(clazz, callable))
        }

        return result!!
    }

    @JvmName("getPathFun")
    private fun getPath(): Path {
        val annotation = callable.findAnnotation<Definition>() ?: throw NullPointerException().wrap(clazz, callable)
        return PathFactory.create(eventType, annotation.path)
    }
}