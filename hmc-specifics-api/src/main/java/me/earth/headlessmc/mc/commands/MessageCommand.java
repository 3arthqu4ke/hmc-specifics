package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.player.Player;

public class MessageCommand extends PrefixedCommand implements ScheduledCommand {
    public MessageCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "msg", "Sends a chat message.", "msg ", "message");
    }

    @Override
    protected void executeUnprefixed(String line, String unprefixedLine, String... args) {
        Player player = mc.getPlayer();
        if (player == null) {
            ctx.log("You need to be ingame to send a message!");
        } else {
            player.sendMessage(unprefixedLine);
        }
    }

}
