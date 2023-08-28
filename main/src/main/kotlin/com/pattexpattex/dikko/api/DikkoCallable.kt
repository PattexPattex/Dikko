package com.pattexpattex.dikko.api

import kotlin.reflect.KParameter

internal interface DikkoCallable<T> {
    suspend fun call(args: Map<KParameter, Any?> = emptyMap()) = callBlocking(args)
    fun callBlocking(args: Map<KParameter, Any?> = emptyMap()): Result<T> { throw UnsupportedOperationException() }
}