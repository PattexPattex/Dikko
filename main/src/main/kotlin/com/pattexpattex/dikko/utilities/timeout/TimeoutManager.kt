package com.pattexpattex.dikko.utilities.timeout

import dev.minn.jda.ktx.events.getDefaultScope
import dev.minn.jda.ktx.util.SLF4J
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitAll
import net.dv8tion.jda.api.entities.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class TimeoutManager() {
    constructor(map: Map<String, Timeout>) : this() {
        _map.putAll(map)
    }

    constructor(manager: TimeoutManager) : this(manager._map)

    private val _map = hashMapOf<String, Timeout>()
    val map: Map<String, Timeout> get() = _map

    fun create(
        id: String,
        timeout: Duration = 5.minutes,
        start: Boolean = false,
        runPrevious: Boolean = false,
        scope: CoroutineScope = defaultScope,
        action: () -> Unit
    ) = _start(Timeout(id, timeout, scope, action), start, runPrevious)

    fun create(
        id: String,
        message: Message,
        timeout: Duration = 5.minutes,
        start: Boolean = false,
        runPrevious: Boolean = false,
        scope: CoroutineScope = defaultScope
    ) = _start(MessageTimeout(id, message, scope, timeout), start, runPrevious)

    private fun <T : Timeout> _start(timeout: T, start: Boolean, runPrevious: Boolean): T {
        if (runPrevious) {
            val def = runNowAsync(timeout.id)
        }

        cancel(timeout.id)

        if (start) {
            timeout.start()
        }

        _map[timeout.id] = timeout
        return timeout
    }

    fun start(id: String) = _map[id]?.start()
    fun cancel(id: String) = _map[id]?.cancel()
    suspend fun runNow(id: String) = _map[id]?.runNow()
    fun runNowAsync(id: String) = _map[id]?.runNowAsync()
    fun restart(id: String) = _map[id]?.restart()

    fun startAll(): TimeoutManager {
        _map.values.forEach(Timeout::start)
        return this
    }

    fun cancelAll(): TimeoutManager {
        _map.values.forEach(Timeout::cancel)
        return this
    }

    suspend fun runNowAll(): TimeoutManager {
        _map.values
            .map(Timeout::runNowAsync)
            .awaitAll()

        return this
    }

    fun runNowAllAsync(): TimeoutManager {
        _map.values.map(Timeout::runNowAsync)
        return this
    }

    fun restartAll(): TimeoutManager {
        _map.values.forEach(Timeout::restart)
        return this
    }

    fun remove(id: String) = _map.remove(id)?.also { it.cancel() }

    fun removeAll(): TimeoutManager {
        cancelAll()
        _map.clear()
        return this
    }

    operator fun get(id: String) = _map[id]
    @JvmName("getInline") inline operator fun <reified T : Timeout> get(id: String) = get(id) as? T?

    private val defaultScope get() = getDefaultScope(errorHandler = CoroutineExceptionHandler { _, t -> log(t) })

    private val log by SLF4J
    private fun log(t: Throwable) = log.error("Timeout action threw an exception", t)
}