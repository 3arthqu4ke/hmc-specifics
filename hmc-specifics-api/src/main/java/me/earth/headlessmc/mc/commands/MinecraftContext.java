package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.Command;
import me.earth.headlessmc.api.command.CommandContextImpl;
import me.earth.headlessmc.api.command.impl.HelpCommand;
import me.earth.headlessmc.api.command.impl.MemoryCommand;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.SpecificProperties;
import me.earth.headlessmc.mc.auth.AccountRefreshingService;
import me.earth.headlessmc.runtime.commands.RuntimeQuitCommand;

import java.util.List;
import java.util.Map;

public class MinecraftContext extends CommandContextImpl {
    private final AccountRefreshingService accountRefreshingService = new AccountRefreshingService();
    public final Minecraft mc;

    public MinecraftContext(HeadlessMc ctx, Minecraft mc) {
        super(ctx);
        this.mc = mc;
        add(new QuitCommand(ctx, mc));
        add(new HelpCommand(ctx));
        add(new RenderCommand(ctx, mc));
        add(new ClickCommand(ctx, mc));
        add(new GuiCommand(ctx, mc));
        add(new TextCommand(ctx, mc));
        add(new CloseCommand(ctx, mc));
        add(new MenuCommand(ctx, mc));
        add(new MemoryCommand(ctx));
        add(new ConnectCommand(ctx, mc));
        add(new DisconnectCommand(ctx, mc));
        add(new LoginCommand(ctx, accountRefreshingService, mc));
        add(new KeyCommand(ctx, mc));
        add(new MessageCommand(ctx, mc));
        add(new CommandCommand(ctx, mc));
        add(new DotMessageCommand(ctx, mc));
        if (ctx.getConfig().get(
            SpecificProperties.KEEP_RUNTIME_COMMANDS, false)) {
            for (Command command : ctx.getCommandLine().getCommandContext()) {
                if (!(command instanceof RuntimeQuitCommand)) {
                    this.add(command);
                }
            }
        }
    }

    @Override
    public void add(Command command) {
        super.add(command);
    }

    @Override
    public List<Map.Entry<String, String>> getCompletions(String line) {
        return super.getCompletions(line);
    }

    @Override
    protected void executeCommand(Command cmd, String line, String... args) {
        if (cmd instanceof ScheduledCommand) {
            mc.scheduleEx(() -> super.executeCommand(cmd, line, args));
        } else {
            super.executeCommand(cmd, line, args);
        }
    }

    public AccountRefreshingService getAccountRefreshingService() {
        return accountRefreshingService;
    }

}
