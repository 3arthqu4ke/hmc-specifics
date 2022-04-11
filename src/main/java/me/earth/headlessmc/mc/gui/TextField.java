package me.earth.headlessmc.mc.gui;

/**
 * Text fields belong to {@link GuiScreen}s.
 */
public interface TextField extends GuiElement {
    /**
     * Sets the text returned by {@link GuiElement#getText()} for this object.
     *
     * @param text the text.
     */
    void setText(String text);

}
