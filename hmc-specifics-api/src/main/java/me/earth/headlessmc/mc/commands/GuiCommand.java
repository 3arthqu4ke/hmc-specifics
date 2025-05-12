package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.api.command.CommandUtil;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.GuiButton;
import me.earth.headlessmc.mc.gui.GuiElement;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.gui.TextField;
import me.earth.headlessmc.mc.util.ExtendedTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GuiCommand extends AbstractGuiCommand
    implements ScheduledCommand {
    public GuiCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "gui", "Dumps the currently displayed screen.");
    }

    @Override
    protected void execute(GuiScreen gui, String... args) throws CommandException {
        String tooltip = CommandUtil.getOption("--tooltip", args);
        if (tooltip != null) {
            List<String> tooltipLines = gui.getAllElements()
                    .stream()
                    .filter(e -> tooltip.equals(String.valueOf(e.getId())))
                    .map(GuiElement::getTooltip)
                    .findFirst()
                    .orElse(null);

            if (tooltipLines == null || tooltipLines.isEmpty()) {
                ctx.log("No tooltip found.");
            } else {
                tooltipLines.forEach(ctx::log);
            }

            return;
        } else if (CommandUtil.hasFlag("--tooltip", args)) {
            throw new CommandException("Please specify an element to get the tooltip of.");
        }

        List<GuiButton> guiButtons = gui.getButtons();
        guiButtons.sort(Comparator.comparingInt(GuiElement::getId));

        List<TextField> textFields = gui.getTextFields();
        textFields.sort(Comparator.comparingInt(GuiElement::getId));
        boolean verbose = Arrays.asList(args).contains("-v");

        List<GuiElement> all = new ArrayList<>(gui.getAllElements());
        all.removeAll(guiButtons);
        all.removeAll(textFields);
        all.sort(Comparator.comparingInt(GuiElement::getId));

        ctx.log(String.format(
            "Screen: %s\nButtons:\n%s\nTextFields:\n%s",
            gui.getHandle().getClass().getName(),
            table(guiButtons, verbose).insert("on", "type", g -> g.isEnabled() ? "1" : "0").build(),
            table(textFields, verbose).build()));
        
        if (!all.isEmpty()) {
            ctx.log("Other:\n" + table(all, verbose).buildCalculatingAnsiWidth());
        }
    }

    private <T extends GuiElement> ExtendedTable<T> table(Iterable<T> elements, boolean verbose) {
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
