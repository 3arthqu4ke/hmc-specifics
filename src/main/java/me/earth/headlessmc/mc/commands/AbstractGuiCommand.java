package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.GuiScreen;

// TODO: make this an interface
public abstract class AbstractGuiCommand extends AbstractMinecraftCommand {
    public AbstractGuiCommand(HeadlessMc ctx, Minecraft mc,
                              String name, String desc) {
        super(ctx, mc, name, desc);
    }

    @Override
    public void execute(String... args) throws CommandException {
        GuiScreen gui = mc.getScreen();
        if (gui == null) {
            ctx.log("Minecraft is currently not displaying a Gui.");
        } else {
            execute(gui, args);
        }
    }

    protected abstract void execute(GuiScreen gui, String... args)
        throws CommandException;

}
