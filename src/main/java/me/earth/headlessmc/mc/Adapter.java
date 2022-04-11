package me.earth.headlessmc.mc;

/**
 * An Adapter for Minecraft's classes.
 */
public interface Adapter {
    /**
     * The object whose methods and fields are being accessed.
     *
     * @return the object this adapter "adapts".
     */
    default Object getHandle() {
        return this;
    }

}
