package com.pattexpattex.dikko.utilities.timeout

import dev.minn.jda.ktx.util.SLF4J
import kotlinx.coroutines.*
import java.time.Instant
import kotlin.properties.Delegates.notNull
import kotlin.time.Duration

open class Timeout internal constructor(
    val id: String,
    val timeout: Duration,
    private val scope: CoroutineScope,
    protected val action: suspend () -> Unit
) {
    protected val log by SLF4J
    private var job: Job = Job()
    private val result: CompletableDeferred<Timeout> = CompletableDeferred()

    var isCancelled = false
        protected set

    var startTime by notNull<Instant>()
        private set

    val endTime: Instant get() = startTime.plusMillis(timeout.inWholeMilliseconds)

    fun start(): Timeout {
        if (!isCancelled) {
            createJob()
        }

        return this
    }

    fun restart(): Timeout {
        if (isCancelled) {
            return this
        }

        createJob()
        return this
    }

    suspend fun runNow(): Timeout {
        cancel()
        doAction()
        result.complete(this)
        return this
    }

    fun runNowAsync() = scope.async { runNow() }

    suspend fun await() = result.await()

    open fun cancel(message: String? = null): Timeout {
        isCancelled = true
        job.cancel()
        return this
    }

    protected open suspend fun doAction(): Unit = action()

    private fun createJob() {
        startTime = Instant.now()

        job.cancel()
        job = scope.launch {
            delay(timeout)
            doAction()
            result.complete(this@Timeout)
        }
    }

    protected fun log(t: Throwable) = log("Timeout action threw an exception", t)
    protected fun log(msg: String, t: Throwable) = log.warn("[Timeout $id] $msg", t)
}