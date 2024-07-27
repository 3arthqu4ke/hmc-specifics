package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.command.AbstractCommand;
import me.earth.headlessmc.mc.Minecraft;

public abstract class AbstractMinecraftCommand extends AbstractCommand {
    protected final Minecraft mc;

    public AbstractMinecraftCommand(HeadlessMc ctx, Minecraft mc,
                                    String name, String desc) {
        super(ctx, name, desc);
        this.mc = mc;
    }

}
