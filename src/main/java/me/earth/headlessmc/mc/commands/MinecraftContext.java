package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.Command;
import me.earth.headlessmc.command.CommandContextImpl;
import me.earth.headlessmc.command.HelpCommand;
import me.earth.headlessmc.command.MemoryCommand;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.SpecificProperties;
import me.earth.headlessmc.runtime.commands.RuntimeQuitCommand;

public class MinecraftContext extends CommandContextImpl {
    protected final Minecraft mc;

    public MinecraftContext(HeadlessMc ctx, Minecraft mc) {
        super(ctx);
        this.mc = mc;
        add(new QuitCommand(ctx, mc));
        add(new HelpCommand(ctx));
        add(new MessageCommand(ctx, mc));
        add(new RenderCommand(ctx, mc));
        add(new ClickCommand(ctx, mc));
        add(new GuiCommand(ctx, mc));
        add(new TextCommand(ctx, mc));
        add(new CloseCommand(ctx, mc));
        add(new MenuCommand(ctx, mc));
        add(new MemoryCommand(ctx));
        add(new ConnectCommand(ctx, mc));
        add(new DisconnectCommand(ctx, mc));
        if (ctx.getConfig().get(
            SpecificProperties.KEEP_RUNTIME_COMMANDS, false)) {
            for (Command command : ctx.getCommandContext()) {
                if (!(command instanceof RuntimeQuitCommand)) {
                    this.add(command);
                }
            }
        }
    }

    @Override
    protected void executeCommand(Command cmd, String... args) {
        if (cmd instanceof ScheduledCommand) {
            mc.scheduleEx(() -> super.executeCommand(cmd, args));
        } else {
            super.executeCommand(cmd, args);
        }
    }

}
