package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.mc.Minecraft;

public class DisconnectCommand extends AbstractMinecraftCommand
    implements ScheduledCommand {
    public DisconnectCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "disconnect", "Disconnects you from a server.");
    }

    @Override
    public void execute(String line, String... args) throws CommandException {
        ctx.log("Disconnecting...");
        mc.disconnect();
    }

}
