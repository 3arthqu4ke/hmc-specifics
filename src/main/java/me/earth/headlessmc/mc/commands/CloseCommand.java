package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.player.Player;

public class CloseCommand extends AbstractPlayerCommand
    implements ScheduledCommand {
    public CloseCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "close", "Closes the current screen, if ingame.");
    }

    @Override
    protected void execute(Player player, String... args) {
        ctx.log("Closing current screen...");
        player.closeScreen();
    }

}
