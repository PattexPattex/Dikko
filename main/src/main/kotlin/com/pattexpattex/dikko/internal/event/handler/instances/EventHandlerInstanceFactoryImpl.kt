package com.pattexpattex.dikko.internal.event.handler.instances

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.DikkoCallable
import com.pattexpattex.dikko.api.ctx.GuildContext
import com.pattexpattex.dikko.api.event.handler.EventHandlerInstanceFactory
import com.pattexpattex.dikko.internal.exception.ExceptionTools.wrap
import com.pattexpattex.dikko.internal.exception.ReflectCallException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

internal class EventHandlerInstanceFactoryImpl(private val clazz: KClass<*>) : EventHandlerInstanceFactory, DikkoCallable<Any> {
    override fun createInstance(ctx: GuildContext): Any {
        val objInstance = clazz.objectInstance
        if (objInstance != null) {
            return objInstance
        }

        val constructor = getConstructor()
        val args = createArgs(constructor, ctx)
        return callBlocking(args).getOrThrow()
    }

    private fun getConstructor(): KFunction<Any> {
        return clazz.constructors.find {
            it.parameters.size == 1 && it.parameters[0].type == typeOf<Dikko>()
        } ?: clazz.constructors.find {
            it.parameters.isEmpty()
        } ?: throw NullPointerException("No applicable constructors").wrap(clazz)
    }

    private fun createArgs(constructor: KFunction<*>, ctx: GuildContext): Map<KParameter, Any?> {
        return if (constructor.parameters.isEmpty()) {
            emptyMap()
        } else {
            mapOf(constructor.parameters[0] to ctx)
        }
    }

    override fun callBlocking(args: Map<KParameter, Any?>): Result<Any> {
        val constructor = getConstructor()

        return try {
            constructor.isAccessible = true
            val out = constructor.callBy(args)
            Result.success(out)
        } catch (e: Throwable) {
            Result.failure<Nothing>(ReflectCallException("Failed creating instance of class ${clazz.qualifiedName}", e).wrap(clazz, constructor))
        }
    }
}