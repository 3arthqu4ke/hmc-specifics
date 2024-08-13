package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.player.Player;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void getCompletions(String line, List<Map.Entry<String, String>> completions, String... args) {
        if (line.startsWith("/")) {
            List<Map.Entry<String, String>> mcCompletions = mc.getCompletions(line);
            if (args.length == 1 && !line.endsWith(" ")) {
                mcCompletions.forEach(e -> completions.add(new AbstractMap.SimpleEntry<>("/" + e.getKey(), e.getValue())));
            } else {
                completions.addAll(mcCompletions);
            }
        } else {
            super.getCompletions(line, completions, args);
        }
    }

}
