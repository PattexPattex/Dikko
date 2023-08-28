package com.pattexpattex.dikko.utilities

import dev.minn.jda.ktx.util.SLF4J

/**
 * Gets an instance of a [SLF4J logger][org.slf4j.Logger].
 * @see dev.minn.jda.ktx.util.SLF4J
 */
inline fun <reified T> T.logger() = SLF4J<T>().value