package me.earth.headlessmc.mc.gui;

/**
 * Represents a button belonging to a {@link GuiScreen}.
 */
public interface GuiButton extends GuiElement {
    /**
     * A GuiButton can be enabled or disabled, if it's disabled it can't be
     * clicked.
     *
     * @return {@code true} if the GuiButton represented by this object is
     * enabled.
     */
    boolean isEnabled();

    /**
     * Sets the value returned by {@link #isEnabled()}.
     *
     * @param enabled the value.
     */
    void setEnabled(boolean enabled);

}
