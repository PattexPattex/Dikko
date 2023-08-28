package com.pattexpattex.dikko.api.path

interface PathParameter<T : Path> {
    val value: String
    val index: Int
}