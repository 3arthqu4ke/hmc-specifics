package me.earth.headlessmc.mc.gui;

/**
 * Listener for {@link FontRenderer}. If any of Minecraft's FontRenderers render
 * a String {@link FontRendererListener#onRender(String, float, float)} will be
 * called for this listener.
 */
@FunctionalInterface
public interface FontRendererListener {
    /**
     * Called when one of Minecrafts FontRenderers render a String and this
     * listener is registered on a {@link FontRenderer}.
     *
     * @param text the text which is being rendered.
     * @param x    the x position the text is being rendered at.
     * @param y    the y position the text is being rendered at.
     */
    // TODO: capture shadow, color?
    void onRender(String text, float x, float y);

}
