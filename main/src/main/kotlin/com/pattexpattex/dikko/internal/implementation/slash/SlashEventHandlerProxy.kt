package com.pattexpattex.dikko.internal.implementation.slash

import com.pattexpattex.dikko.internal.event.handler.proxy.EventHandlerProxyImpl
import com.pattexpattex.dikko.internal.path.PathFactory
import com.pattexpattex.dikko.internal.path.matcher.PathMatcherImpl
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class SlashEventHandlerProxy(
    clazz: KClass<*>,
    callable: KCallable<*>
) : EventHandlerProxyImpl(clazz, callable, SlashEventWrapper::class) {
    class Parameter(val optionData: OptionData, val type: KType, val isRequired: Boolean, val skipCheck: Boolean, val name: String)

    val path = PathFactory.create<SlashEventWrapper>((pathMatcher as PathMatcherImpl<*>).pattern) as SlashPath
    val parameters: List<Parameter> = arrayListOf()

    fun addParameter(parameter: Parameter) {
        (parameters as ArrayList).add(parameter)
    }
}