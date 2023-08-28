package com.pattexpattex.dikko.internal.event.handler.instances

import com.pattexpattex.dikko.api.annotations.UseFactory
import com.pattexpattex.dikko.api.event.handler.EventHandlerInstanceFactory
import com.pattexpattex.dikko.internal.exception.ExceptionTools.wrap
import com.pattexpattex.dikko.internal.exception.ReflectCallException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

internal object EventHandlerInstanceFactoryFactory {
    fun createFactory(instanceClass: KClass<*>): EventHandlerInstanceFactory {
        val factoryType = instanceClass.findAnnotation<UseFactory>()?.type

        return if (factoryType == null) {
            EventHandlerInstanceFactoryImpl(instanceClass)
        } else {
            val noArgsConstructor = factoryType.constructors.singleOrNull { it.parameters.all(KParameter::isOptional) }

            try {
                if (factoryType.objectInstance != null) {
                    factoryType.objectInstance!!
                } else {
                    factoryType.createInstance()
                }
            } catch (e: Throwable) {
                throw ReflectCallException("Failed creating factory of factory for class ${instanceClass.qualifiedName}", e).wrap(factoryType, noArgsConstructor)
            }
        }
    }
}