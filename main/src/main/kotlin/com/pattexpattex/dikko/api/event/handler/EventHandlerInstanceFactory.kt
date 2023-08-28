package com.pattexpattex.dikko.api.event.handler

import com.pattexpattex.dikko.api.Dikko

fun interface EventHandlerInstanceFactory {
    fun createInstance(dikko: Dikko): Any
}