package com.pattexpattex.dikko.internal.implementation.modal

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.annotations.canSkipNonCriticalChecks
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.event.dispatcher.EventDispatcherImpl
import com.pattexpattex.dikko.internal.exception.ExceptionTools
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.modals.Modal
import net.dv8tion.jda.api.interactions.modals.ModalMapping
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

class ModalEventDispatcher(dikko: Dikko) : EventDispatcherImpl<Modal>(dikko) {
    override fun registerEventHandler(proxy: EventHandlerProxy) {
        val modal = definitionProxies.find { proxy.pathMatcher.matches(it.path) }
            ?: return log.error("Definition with path '${proxy.pathMatcher}' was not found", ExceptionTools.details(proxy.clazz, proxy.callable))

        if (checkEventHandlerParameters(modal.value, proxy)) {
            super.registerEventHandler(proxy)
        }
    }

    private fun checkEventHandlerParameters(modal: Modal, proxy: EventHandlerProxy): Boolean {
        val components = modal.components
        val issues = arrayListOf<String>()
        var result = true

        fun logIssues() {
            if (issues.isNotEmpty()) {
                log.warn("{}: Issue(s) with event handler definition:", ExceptionTools.format(proxy.clazz, proxy.callable))
                issues.forEach { log.warn("\t{}", it) }
            }

            if (!result) {
                log.warn("\tDisabling event handler, please fix the issues")
            }
        }

        if (proxy.callable.valueParameters.size == 1) {
            return true
        }

        if (proxy.callable.valueParameters.size - 1 != components.size) {
            issues.add("Number of definition components (${components.size}) and event handler parameters (${proxy.callable.valueParameters.size - 1}) do not match")
            logIssues()
            return false
        }

        for (i in components.indices) {
            val component = components[i].components[0] as TextInput
            val parameter = proxy.callable.valueParameters[i + 1]

            if (parameter.type !in listOf(typeOf<ModalMapping>(), typeOf<ModalMapping?>())) {
                issues.add("Invalid parameter type at index $i - Expected: '${ModalMapping::class}', actual: '${parameter.type}'")
                result = false
            }

            if (canSkipNonCriticalChecks(proxy.clazz, proxy.callable, parameter)) {
                continue
            }

            if (component.isRequired && parameter.isOptional) {
                issues.add("Parameter '${parameter.name}' has an unnecessary default value")
            }

            if (component.isRequired && parameter.type.isMarkedNullable) {
                issues.add("Parameter '${parameter.name}' is unnecessarily marked nullable")
            }

            if (parameter.name != component.id) {
                issues.add("Name of parameter '${parameter.name}' does not match id of component in definition ('${component.label}')")
            }
        }

        logIssues()
        return result
    }
}