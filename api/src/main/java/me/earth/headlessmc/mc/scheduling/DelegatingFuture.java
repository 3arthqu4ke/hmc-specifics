package me.earth.headlessmc.mc.scheduling;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * A {@link Future} which delegates all calls to the given one.
 *
 * @param <V> The result type returned by this Future's get method.
 */
public class DelegatingFuture<V> implements Future<V> {
    private final Supplier<V> value;
    private final Future<?> future;

    /**
     * Constructs a new {@link DelegatingFuture}.
     *
     * @param future the wrapped future all calls are delegated to.
     * @param value  supplies the value for this futures get methods.
     */
    public DelegatingFuture(Future<?> future, Supplier<V> value) {
        this.future = future;
        this.value = value;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        future.get();
        return value.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        future.get(timeout, unit);
        return value.get();
    }

}
