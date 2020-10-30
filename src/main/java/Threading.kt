package systems.abacus.threading.helpers

import java.util.concurrent.*

val executor: ExecutorService = Executors.newSingleThreadExecutor()

fun runLocked(semaphore: Semaphore, runnable: Runnable) {
    try {
        semaphore.acquire()
        runnable.run()
    } finally {
        semaphore.release()
    }
}

inline fun <R> runLocked(semaphore: Semaphore, runnable: () -> R): R {
    return try {
        semaphore.acquire()
        runnable.invoke()
    } finally {
        semaphore.release()
    }
}

inline fun <T, R> runLocked(semaphore: Semaphore, param: T, runnable: (T) -> R): R {
    return try {
        semaphore.acquire()
        runnable.invoke(param)
    } finally {
        semaphore.release()
    }
}

inline fun <R> runLockedInOwnThread(semaphore: Semaphore, crossinline runnable: () -> R): Future<R> {
    val callable = Callable {
        try {
            semaphore.acquire()
            runnable.invoke()
        } finally {
            semaphore.release()
        }
    }
    return executor.submit(callable)
}

inline fun <T, R> runLockedInOwnThread(semaphore: Semaphore, param: T, crossinline runnable: (T) -> R): Future<R> {
    val callable = Callable {
        try {
            semaphore.acquire()
            runnable.invoke(param)
        } finally {
            semaphore.release()
        }
    }
    return executor.submit(callable)
}