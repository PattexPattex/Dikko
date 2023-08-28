package com.pattexpattex.dikko.api.annotations

/**
 * Dikko will run an annotated function after its initial setup.
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class AfterSetup
