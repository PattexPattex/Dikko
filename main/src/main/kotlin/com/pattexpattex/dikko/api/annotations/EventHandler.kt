package com.pattexpattex.dikko.api.annotations

/**
 * Register an event handler function.
 * The type of the first parameter of the function determines the type of events this handler will receive.
 *
 * @see com.pattexpattex.dikko.api.event.EventWrapper
 * @see com.pattexpattex.dikko.api.path.PathMatcher
 * @see com.pattexpattex.dikko.api.annotations.Definition
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class EventHandler(val pathPattern: String)
