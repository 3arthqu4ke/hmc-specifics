package me.earth.headlessmc.mc.scheduling;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * In an environment with a main thread this type allows you to schedule tasks
 * on that thread, from other threads.
 */
@FunctionalInterface
public interface SchedulesTasks {
    /**
     * Schedules the given task to run on the main thread. If the Thread this
     * method is called on is the main thread the task might be run
     * immediately.
     *
     * @param task the task to run on the main thread.
     */
    Future<?> schedule(Runnable task);

    /**
     * Schedules the given {@link Supplier} on the main thread. The returned
     * {@link Future} will yield the result of the Supplier's get method once it
     * completed. If this method is called on the main thread it might be
     * executed immediately.
     *
     * @param supplier the supplier which is scheduled on the main thread.
     * @param <T>      the type of the value returned by the supplier.
     * @return a Future for the given Supplier.
     */
    default <T> Future<T> schedule(Supplier<T> supplier) {
        AtomicReference<T> result = new AtomicReference<>();
        Future<?> future = schedule(() -> result.set(supplier.get()));
        return new DelegatingFuture<>(future, result::get);
    }

}
