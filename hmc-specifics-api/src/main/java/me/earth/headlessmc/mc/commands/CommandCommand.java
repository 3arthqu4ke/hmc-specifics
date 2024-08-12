package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.player.Player;

public class CommandCommand extends PrefixedCommand implements ScheduledCommand {
    public CommandCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "/", "Sends a chat command.", "/", "command");
    }

    @Override
    protected void executeUnprefixed(String line, String unprefixedLine, String... args) {
        Player player = mc.getPlayer();
        if (player == null) {
            ctx.log("You need to be ingame to send a command!");
        } else {
            player.sendMessage("/" + unprefixedLine);
        }
    }

}
