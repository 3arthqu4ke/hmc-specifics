package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.player.Player;

// TODO: make this an interface
public abstract class AbstractPlayerCommand extends AbstractMinecraftCommand {
    public AbstractPlayerCommand(HeadlessMc ctx, Minecraft mc,
                                 String name, String desc) {
        super(ctx, mc, name, desc);
    }

    protected abstract void execute(Player player, String... args);

    @Override
    public void execute(String line, String... args) throws CommandException {
        Player player = mc.getPlayer();
        if (player == null) {
            throw new CommandException("You are not ingame!");
        }

        this.execute(player, args);
    }


}
