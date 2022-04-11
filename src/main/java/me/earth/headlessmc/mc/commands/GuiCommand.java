package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.GuiButton;
import me.earth.headlessmc.mc.gui.GuiElement;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.gui.TextField;
import me.earth.headlessmc.mc.util.ExtendedTable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GuiCommand extends AbstractGuiCommand
    implements ScheduledCommand {
    public GuiCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "gui", "Dumps the currently displayed screen.");
    }

    @Override
    protected void execute(GuiScreen gui, String... args) {
        List<GuiButton> guiButtons = gui.getButtons();
        guiButtons.sort(Comparator.comparingInt(GuiElement::getId));
        List<TextField> textFields = gui.getTextFields();
        textFields.sort(Comparator.comparingInt(GuiElement::getId));
        boolean verbose = Arrays.asList(args).contains("-v");
        ctx.log(String.format(
            "Screen: %s\nButtons:\n%s\nTextFields:\n%s",
            gui.getHandle().getClass().getName(),
            table(guiButtons, verbose)
                .insert("on", "type", g -> g.isEnabled() ? "1" : "0").build(),
            table(textFields, verbose).build()));
    }

    private <T extends GuiElement> ExtendedTable<T> table(Iterable<T> elements,
                                                          boolean verbose) {
        return new ExtendedTable<T>()
            .addAll(elements)
            .withInt("id", GuiElement::getId)
            .withColumn("text", GuiElement::getText)
            .withInt("x", GuiElement::getX)
            .withInt("y", GuiElement::getY)
            .withInt("w", GuiElement::getWidth)
            .withInt("h", GuiElement::getHeight)
            .withColumn("type", g -> verbose
                ? g.getHandle().getClass().getName()
                : g.getHandle().getClass().getSimpleName());
    }

}
