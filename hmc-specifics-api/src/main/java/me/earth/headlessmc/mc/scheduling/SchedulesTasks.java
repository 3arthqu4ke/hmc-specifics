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
     * @return a Future representing the completion of the task.
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
        Future<?> future = scheduleEx(() -> result.set(supplier.get()));
        return new DelegatingFuture<>(future, result::get);
    }

    /**
     * Same as {@link #schedule(Runnable)} but the runnable will be wrapped in
     * a try- and catch- which will print the StackTrace of any caught Throwable
     * and then immediately rethrows it. The reason for this is that sometimes
     * Exceptions just get swallowed by the Scheduler implementation (E.g. this
     * seems to be the case on Minecraft 1.19.4)
     *
     * @param task the task to run on the main thread.
     * @return a Future representing the completion of the task.
     */
    default Future<?> scheduleEx(Runnable task) {
        return schedule(() -> {
            try {
                task.run();
            } catch (Throwable t) {
                t.printStackTrace(System.err);
                throw t;
            }
        });
    }

}
