package com.athaydes.kanvas

import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * The ID of a task added to a [Scheduler].
 */
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

/**
 * A helper class that makes it simpler to run tasks with a desired maximum frequency.
 *
 * A task is added to this object by calling [Scheduler.add], which returns a [TaskId]
 * which can then be used to request invocations of the task via the [Scheduler.requestExecution] method.
 *
 * The task is only executed with a maximum frequency determined by the [Duration] argument
 * provided to the [Scheduler.add] call. If more requests arrive, they cause a single new execution
 * to be scheduled such that the maximum frequency is honoured.
 *
 * To cancel a task,
 */
class Scheduler {
    private val _tasks = mutableMapOf<TaskId, Task>()
    private val _idCounter = AtomicInteger()

    private val _executor = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable).apply {
            isDaemon = true
            name = "Main Kanvas Scheduler"
        }
    }

    /**
     * Forget all tasks.
     */
    fun clear() {
        _executor.submit {
            _tasks.clear()
        }
        _idCounter.set(0)
    }

    /**
     * Create a new task that executes the provided [Runnable] with at most the requested frequency.
     *
     * To actually execute the Task, use [requestExecution].
     *
     * @param maxFrequency the maximum allowed frequency with which the task should run
     * @param runnable the Task action
     * @return the task ID
     */
    fun add(maxFrequency: Duration, runnable: Runnable): TaskId {
        val id = TaskId(_idCounter.incrementAndGet())
        _executor.submit {
            _tasks[id] = Task(runnable, maxFrequency)
        }
        return id
    }

    /**
     * Remove a task with the given ID.
     *
     * If there's no such task, this call has no effect.
     * @param taskId ID
     */
    fun remove(taskId: TaskId) {
        _executor.submit { _tasks.remove(taskId) }
    }

    /**
     * Request that a previously added task with the given ID executes immediately,
     * or at the nearest possible time in which the maximum execution frequency of the task
     * is honoured.
     */
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