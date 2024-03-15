package com.athaydes.kanvas

import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class TaskId internal constructor(private val _id: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return (other as? TaskId)?._id == _id
    }

    override fun hashCode(): Int = _id
}

private class Task(
    val runnable: Runnable,
    val maxFrequency: Duration
) {
    var lastRun: Instant = Instant.EPOCH
    var scheduled: Boolean = false
}

object Scheduler {
    private val _tasks = mutableMapOf<TaskId, Task>()
    private val _idCounter = AtomicInteger()

    private val _executor = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable).apply {
            isDaemon = true
            name = "Main Kanvas Scheduler"
        }
    }

    @JvmStatic
    fun add(maxFrequency: Duration, runnable: Runnable): TaskId {
        val id = TaskId(_idCounter.incrementAndGet())
        _executor.submit {
            _tasks[id] = Task(runnable, maxFrequency)
        }
        return id
    }

    @JvmStatic
    fun requestExecution(id: TaskId) {
        _executor.submit {
            val task = _tasks[id] ?: throw RuntimeException("Task does not exist with ID=$id")
            if (task.scheduled) return@submit
            val nextAcceptableRun = task.lastRun + task.maxFrequency
            val msToNextRun = nextAcceptableRun.toEpochMilli() - System.currentTimeMillis()
            if (msToNextRun <= 0L) {
                runNow(task)
            } else {
                task.scheduled = true
                _executor.schedule({
                    runNow(task)
                }, msToNextRun, TimeUnit.MILLISECONDS)
            }
        }.get()
    }

    private fun runNow(task: Task) {
        task.lastRun = Instant.now()
        Thread.startVirtualThread {
            try {
                task.runnable.run()
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                task.scheduled = false
            }
        }
    }

}