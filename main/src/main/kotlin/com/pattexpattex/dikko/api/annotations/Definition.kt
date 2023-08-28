package com.pattexpattex.dikko.api.annotations

/**
 * Register a definition. It can be a button, a slash command, modal, etc.
 *
 * @see com.pattexpattex.dikko.api.definition.DefinitionProxy
 * @see com.pattexpattex.dikko.api.path.Path
 * @see com.pattexpattex.dikko.api.annotations.EventHandler
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Definition(val path: String)