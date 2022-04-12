package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.player.Player;

public class MenuCommand extends AbstractPlayerCommand
    implements ScheduledCommand {
    public MenuCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "menu", "Opens the menu if you are ingame.");
    }

    @Override
    protected void execute(Player player, String... args) {
        ctx.log("Opening menu...");
        player.openMenu();
    }

}
