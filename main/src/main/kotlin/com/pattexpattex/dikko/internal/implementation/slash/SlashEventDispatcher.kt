package com.pattexpattex.dikko.internal.implementation.slash

import com.pattexpattex.dikko.api.Dikko
import com.pattexpattex.dikko.api.annotations.canSkipNonCriticalChecks
import com.pattexpattex.dikko.api.definition.types.DikkoSlashCommandData
import com.pattexpattex.dikko.api.definition.types.DikkoSlashGroup
import com.pattexpattex.dikko.api.event.handler.EventHandlerProxy
import com.pattexpattex.dikko.internal.DikkoImpl
import com.pattexpattex.dikko.internal.contentToString
import com.pattexpattex.dikko.internal.event.dispatcher.EventDispatcherImpl
import com.pattexpattex.dikko.internal.exception.ExceptionTools.details
import com.pattexpattex.dikko.internal.exception.ExceptionTools.format
import com.pattexpattex.dikko.internal.exception.ExceptionTools.wrap
import com.pattexpattex.dikko.internal.implementation.slashgroup.DikkoSlashGroupImpl
import com.pattexpattex.dikko.internal.path.matcher.PathMatcherImpl
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import kotlin.reflect.full.valueParameters

class SlashEventDispatcher internal constructor(dikko: Dikko) : EventDispatcherImpl<DikkoSlashCommandData>(dikko) {
    override fun registerEventHandler(proxy: EventHandlerProxy) {
        proxy as SlashEventHandlerProxy

        val data = definitionProxies.find { (proxy.pathMatcher as PathMatcherImpl).pattern.startsWith(it.path.value) }
            ?: return log.error("Definition with path '${proxy.pathMatcher}' was not found", details(proxy.clazz, proxy.callable))

        mapEventHandlerParameters(data.value.getSubcommand(proxy.path)?.options ?: data.value.options, proxy)

        if (!checkEventHandlerParameters(proxy, data.value)) {
            return
        }

        super.registerEventHandler(proxy)
    }

    override suspend fun finalizeSetup(jda: JDA) {
        jda.updateCommands()
            .addCommands(definitionProxies.map { it.value })
            .queue()
        addCommandsToGroups()
    }

    private fun mapEventHandlerParameters(options: List<OptionData>, proxy: SlashEventHandlerProxy) {
        for (i in options.indices) {
            val option = options[i]
            val parameter = proxy.callable.valueParameters.getOrNull(i + 1)
                ?: throw NullPointerException("Missing parameter '${option.name}' at index $i").wrap(proxy.clazz, proxy.callable)

            proxy.addParameter(SlashEventHandlerProxy.Parameter(
                option,
                parameter.type,
                !parameter.isOptional,
                canSkipNonCriticalChecks(parameter),
                parameter.name ?: "<null>"
            ))
        }
    }

    private fun checkEventHandlerParameters(proxy: SlashEventHandlerProxy, data: DikkoSlashCommandData): Boolean {
        val options = data.getSubcommand(proxy.path)?.options ?: data.options
        val issues = arrayListOf<String>()
        var result = true

        fun logIssues() {
            if (issues.isNotEmpty()) {
                log.warn("{}: Issue(s) with event handler definition:", format(proxy.clazz, proxy.callable))
                issues.forEach { log.warn("\t{}", it) }

                if (!result) {
                    log.warn("\tDisabling event handler, please fix the issues")
                }
            }
        }

        if (proxy.parameters.size != options.size) {
            issues.add("Number of definition parameters (${options.size}) and event handler parameters (${proxy.parameters.size}) do not match")
            logIssues()
            return false
        }

        for (i in options.indices) {
            val option = options[i]
            val parameter = proxy.parameters[i]

            val acceptableTypes = option.acceptableTypes()
            val parameterType = parameter.type
            if (parameterType !in acceptableTypes) {
                val isOk = acceptableTypes.any { (it.classifier == parameterType.classifier) && (it.isMarkedNullable && !parameter.isRequired) }

                if (!isOk) {
                    issues.add("Invalid parameter type at index $i - Expected one of: '${acceptableTypes.contentToString()}', actual: '${parameter.type}'")
                    result = false
                }
            }

            if (parameter.skipCheck || canSkipNonCriticalChecks(proxy.clazz, proxy.callable)) {
                continue
            }

            if (option.isRequired && !parameter.isRequired) {
                issues.add("Parameter '${parameter.name}' has an unnecessary default value")
            }

            if (option.isRequired && parameter.type.isMarkedNullable) {
                issues.add("Parameter '${parameter.name}' is unnecessarily marked nullable")
            }

            if (option.name != parameter.name) {
                issues.add("Name of parameter '${parameter.name}' does not match name of equivalent option in definition ('${option.name}')")
            }
        }

        logIssues()
        return result
    }

    private fun addCommandsToGroups() {
        val groupManager = (dikko as DikkoImpl).dispatcherManager.dispatchers.slashGroup ?: return

        for (proxy in definitionProxies) {
            val groupId = proxy.value.groupId ?: DikkoSlashGroup.UNGROUPED.id
            val group = groupManager.proxies[groupId]?.value as DikkoSlashGroupImpl?

            if (group == null) {
                log.error("Group definition with path '${proxy.value.groupId}' was not found")
                continue
            }

            group.addCommand(proxy)
        }
    }
}