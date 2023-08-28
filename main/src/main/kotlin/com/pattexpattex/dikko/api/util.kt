package com.pattexpattex.dikko.api

import com.pattexpattex.dikko.api.event.EventWrapper
import com.pattexpattex.dikko.api.event.dispatcher.EventDispatcher
import com.pattexpattex.dikko.internal.DikkoImpl
import kotlin.reflect.KClass

/**
 * Add event paths for Dikko to ignore.
 * These events will not be handled until the paths are removed by [Dikko.handlePaths].
 * @param T Type of events to ignore
 */
inline fun <reified T : EventWrapper> Dikko.ignorePaths(vararg paths: String) = ignorePaths(T::class, *paths)

/**
 * Retrieve an instance of an [EventDispatcher].
 */
inline fun <reified T : EventDispatcher<*>> EventWrapper.getDispatcher() = getDispatcher(T::class)

/**
 * @see getDispatcher
 */
fun <T : EventDispatcher<*>> EventWrapper.getDispatcher(type: KClass<T>) = (dikko as DikkoImpl).dispatcherManager.dispatchers[type]