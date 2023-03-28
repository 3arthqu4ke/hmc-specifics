package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.command.CommandUtil;
import me.earth.headlessmc.mc.Minecraft;

public class QuitCommand extends AbstractMinecraftCommand {
    public QuitCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "quit", "Quits the running game.");
    }

    @Override
    public void execute(String... args) {
        if (CommandUtil.hasFlag("-force", args)) {
            ctx.log("Forcing Minecraft to Exit!");
            System.exit(0);
            return;
        }

        if (CommandUtil.hasFlag("-s", args)) {
            ctx.log("Quitting Minecraft...");
            mc.quit();
        } else {
            mc.scheduleEx(() -> {
                ctx.log("Quitting Minecraft...");
                mc.quit();
            });
        }
    }

    @Override
    public boolean matches(String... args) {
        if (args.length > 0) {
            String lower = args[0].toLowerCase();
            return lower.equalsIgnoreCase("quit")
                || lower.equalsIgnoreCase("exit")
                || lower.equalsIgnoreCase("stop");
        }

        return false;
    }

}
