package me.earth.headlessmc.mc.gui;

import me.earth.headlessmc.mc.Minecraft;

/**
 * Represents {@link Minecraft}'s FontRenderers. {@link FontRendererListener}s
 * registered on this FontRenderer will be notified via {@link
 * FontRendererListener#onRender(String, float, float)} if this FontRenderer
 * renders a String.
 */
public interface FontRenderer {
    /**
     * Registers a {@link FontRendererListener}. When a FontRenderer renders a
     * String all {@link FontRendererListener#onRender(String, float, float)}
     * will be called for every listener registered this way.
     *
     * @param listener the listener to register.
     */
    void register(FontRendererListener listener);

    /**
     * Unregisters a listener registered via {@link FontRenderer#register(FontRendererListener)}.
     *
     * @param listener the listener to unregister.
     */
    void unregister(FontRendererListener listener);

}
