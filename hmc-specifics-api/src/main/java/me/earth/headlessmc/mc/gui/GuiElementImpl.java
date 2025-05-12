package me.earth.headlessmc.mc.gui;

import java.util.List;
import java.util.Objects;

public class GuiElementImpl implements GuiElement {
    private final String text;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int id;
    private final List<String> tooltip;

    public GuiElementImpl(String text, int x, int y, int width, int height, int id, List<String> tooltip) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
        this.tooltip = tooltip;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public List<String> getTooltip() {
        return tooltip;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GuiElementImpl that = (GuiElementImpl) o;
        return x == that.x && y == that.y && width == that.width && height == that.height && id == that.id && Objects.equals(text, that.text) && Objects.equals(tooltip, that.tooltip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, x, y, width, height, id, tooltip);
    }

    @Override
    public String toString() {
        return "GuiElementImpl{" +
                "text='" + text + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", id=" + id +
                ", tooltip='" + tooltip + '\'' +
                '}';
    }

}
