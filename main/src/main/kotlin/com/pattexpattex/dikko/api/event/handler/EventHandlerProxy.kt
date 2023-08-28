package com.pattexpattex.dikko.api.event.handler

import com.pattexpattex.dikko.api.DikkoCallable
import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.path.PathMatcher
import net.dv8tion.jda.api.Permission
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

internal interface EventHandlerProxy : DikkoCallable<Any?> {
    val clazz: KClass<*>
    val callable: KCallable<*>
    val eventType: KClass<out EventWrapper>
    val pathMatcher: PathMatcher<*>
    val requiredPermissions: List<Permission>
    val requiredUserPermissions: List<Permission>
    val isPrivateOnly: Boolean
    val isGuildOnly: Boolean
}