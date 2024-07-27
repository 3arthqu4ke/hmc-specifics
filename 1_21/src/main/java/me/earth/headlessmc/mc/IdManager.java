package me.earth.headlessmc.mc;

import java.util.concurrent.atomic.AtomicInteger;

public enum IdManager {
    INSTANCE;

    private final AtomicInteger id = new AtomicInteger();
    private Object lastContext = new Object();
    private Object context;

    public void setContext(Object context) {
        this.context = context;
    }

    public int getNextId() {
        if (this.context != lastContext) {
            this.lastContext = context;
            id.set(0);
        }

        return id.getAndIncrement();
    }

}
