package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.api.command.ParseUtil;
import me.earth.headlessmc.mc.Minecraft;

public class ConnectCommand extends AbstractMinecraftCommand
    implements ScheduledCommand {
    public ConnectCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "connect", "Connects you to a server.");
    }

    @Override
    public void execute(String... args) throws CommandException {
        if (args.length <= 1) {
            throw new CommandException("Please specify an Ip!");
        }

        int port = 25565;
        if (args.length > 2) {
            port = ParseUtil.parseI(args[2]);
        }

        ctx.log("Connecting to server " + args[1] + " at port " + port + "...");
        mc.connect(args[1], port);
    }

}
