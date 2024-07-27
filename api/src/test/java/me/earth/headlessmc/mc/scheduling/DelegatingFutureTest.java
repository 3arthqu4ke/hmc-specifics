package me.earth.headlessmc.mc.scheduling;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DelegatingFutureTest {
    @Test
    public void testDelegatingFuture_get() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        SchedulesTasks scheduler = service::submit;
        String expected = "Test";
        Future<String> future = scheduler.schedule(() -> expected);
        assertEquals(expected, future.get());
    }

    @Test
    public void testDelegatingFuture_getWithTimeout() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        SchedulesTasks scheduler = service::submit;
        String expected = "Test";
        Future<String> future = scheduler.schedule(() -> expected);
        assertEquals(expected, future.get(60, TimeUnit.SECONDS));
    }

}
