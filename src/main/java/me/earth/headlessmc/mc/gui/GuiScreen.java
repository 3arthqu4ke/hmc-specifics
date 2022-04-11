package me.earth.headlessmc.mc.gui;

import me.earth.headlessmc.mc.Adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a screen displayed by minecraft.
 */
public interface GuiScreen extends Adapter {
    /**
     * Clicks with the given mouse button at the given position on this screen.
     *
     * @param x      the x position to click at.
     * @param y      the y position to click at.
     * @param button the button to use.
     */
    void click(int x, int y, int button);

    /**
     * @return all {@link GuiButton}s of this screen.
     */
    List<GuiButton> getButtons();

    /**
     * @return all {@link TextField}s of this screen.
     */
    List<TextField> getTextFields();

    /**
     * @return a list of all {@link GuiButton}s and {@link TextField}s returned
     * by {@link GuiScreen#getButtons()} and {@link GuiScreen#getTextFields()}.
     */
    default List<GuiElement> getAllElements() {
        List<GuiButton> gbs = getButtons();
        List<TextField> tfs = getTextFields();
        List<GuiElement> elements = new ArrayList<>(gbs.size() + tfs.size());
        elements.addAll(gbs);
        elements.addAll(tfs);
        return elements;
    }

}
