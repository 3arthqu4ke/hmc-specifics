package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.command.CommandUtil;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.GuiButton;
import me.earth.headlessmc.mc.gui.GuiElement;
import me.earth.headlessmc.mc.gui.GuiScreen;

import static me.earth.headlessmc.command.ParseUtil.parseI;

public class ClickCommand extends AbstractGuiCommand
    implements ScheduledCommand {
    public ClickCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "click", "Performs clicks in the Gui.");
    }

    @Override
    public void execute(GuiScreen gui, String... args)
        throws CommandException {
        if (args.length <= 1) {
            throw new CommandException("Please specify where to click.");
        }

        int b = 0;
        int x;
        int y;

        if ("p".equalsIgnoreCase(args[1])) {
            if (args.length < 4) {
                throw new CommandException(
                    "Expected x and y at positions 2 and 3!");
            }

            x = parseI(args[2]);
            y = parseI(args[3]);
            if (args.length > 4) {
                b = parseI(args[4]);
            }
        } else {
            GuiElement element = gui
                .getAllElements()
                .stream()
                .filter(e -> String.valueOf(e.getId()).equals(args[1]))
                .findFirst()
                .orElse(null);

            if (element == null) {
                throw new CommandException(String.format(
                    "Couldn't find GuiElement with id %s", args[1]));
            }

            if (element instanceof GuiButton
                && CommandUtil.hasFlag("-enable", args)) {
                ((GuiButton) element).setEnabled(true);
            }

            x = element.getX() + element.getWidth() / 2;
            y = element.getY() + element.getHeight() / 2;
            if (args.length > 2 && !args[2].equalsIgnoreCase("-enable")) {
                b = parseI(args[2]);
            }
        }

        ctx.log(String.format("Clicking at x=%d, y=%d, button=%d", x, y, b));
        int finalButton = b;
        gui.click(x, y, finalButton);
    }

}
