package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.player.Player;

public class MessageCommand extends AbstractMinecraftCommand
    implements ScheduledCommand {
    public MessageCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "msg", "Sends a chat message.");
    }

    @Override
    public void execute(String... args) throws CommandException {
        if (args.length <= 1) {
            throw new CommandException("Please specify a message!");
        } else if (args.length > 2) {
            ctx.log("MessageCommand received multiple arguments. If you want" +
                        " to send a message containing spaces escape them" +
                        " using \".");
        }

        Player player = mc.getPlayer();
        if (player == null) {
            ctx.log("You need to be ingame to send a message!");
        } else {
            player.sendMessage(args[1]);
        }
    }

}
