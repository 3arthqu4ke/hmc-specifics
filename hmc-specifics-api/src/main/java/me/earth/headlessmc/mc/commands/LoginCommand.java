package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.api.command.CommandUtil;
import me.earth.headlessmc.auth.AbstractLoginCommand;
import me.earth.headlessmc.auth.ValidatedAccount;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.auth.AccountRefreshingService;
import me.earth.headlessmc.mc.auth.McAccount;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;

import java.util.Locale;
import java.util.UUID;

public class LoginCommand extends AbstractLoginCommand {
    protected final AccountRefreshingService accountRefreshingService;
    protected final Minecraft mc;

    public LoginCommand(HeadlessMc ctx, AccountRefreshingService accountRefreshingService, Minecraft mc) {
        super(ctx);
        this.accountRefreshingService = accountRefreshingService;
        this.mc = mc;
    }

    @Override
    public void execute(String line, String... args) throws CommandException {
        String refresh = CommandUtil.getOption("--refresh", args);
        if (refresh == null && CommandUtil.hasFlag("--refresh", args)) {
            refresh = ""; // unknown option to output error
        }

        if (refresh != null) {
            handleRefresh(refresh);
            return;
        }

        if (CommandUtil.hasFlag("-current", args)) {
            McAccount mcAccount = mc.getMcAccount();
            if (mcAccount == null) {
                throw new CommandException("Failed to find account!");
            } else {
                ctx.log("Current account: " + mcAccount.getName());
            }

            return;
        }

        if (CommandUtil.hasFlag("-offline", args)) {
            if (args.length != 5) {
                throw new CommandException("Please specify \"login <name> <id/random> <token> -offline\"");
            }

            String name = args[1];
            UUID id;
            if ("random".equalsIgnoreCase(args[2])) {
                id = UUID.randomUUID();
            } else {
                try {
                    id = UUID.fromString(args[2]);
                } catch (IllegalArgumentException e) {
                    throw new CommandException("Failed to parse \"" + args[2] + "\" as a UUID!");
                }
            }

            McAccount mcAccount = new McAccount(name, id, args[3]);
            ctx.log("Logging in with offline account " + name);
            mc.setMcAccount(mcAccount);
            return;
        }

        super.execute(line, args);
    }

    private void handleRefresh(String refresh) throws CommandException {
        switch (refresh.toLowerCase(Locale.ENGLISH)) {
            case "start":
                if (accountRefreshingService.start(ctx.getConfig(), mc)) {
                    ctx.log("Account refresh started.");
                } else {
                    ctx.log("Account refresh already running.");
                }
                break;
            case "stop":
                if (accountRefreshingService.stop()) {
                    ctx.log("Account refresh stopped.");
                } else {
                    ctx.log("Account refresh not running.");
                }

                break;
            case "status":
                if (accountRefreshingService.isRunning()) {
                    ctx.log("Account refresh running.");
                } else {
                    ctx.log("Account refresh not running.");
                }

                break;
            case "now":
                try {
                    if (accountRefreshingService.refresh(mc, ctx.getConfig())) {
                        ctx.log("Account refreshed successfully.");
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    ctx.log(e.getMessage());
                }

                break;
            default:
                throw new CommandException("Invalid refresh (start/stop/status/now) option: " + refresh);
        }
    }

    @Override
    protected void onSuccessfulLogin(StepFullJavaSession.FullJavaSession fullJavaSession) {
        ValidatedAccount validatedAccount = new ValidatedAccount(fullJavaSession, "");
        accountRefreshingService.setValidatedAccount(validatedAccount);

        McAccount mcAccount = new McAccount(fullJavaSession.getMcProfile().getName(),
                                            fullJavaSession.getMcProfile().getId(),
                                            fullJavaSession.getMcProfile().getMcToken().getAccessToken());
        mc.setMcAccount(mcAccount);
        ctx.log("Logged in with " + mcAccount.getName() + " successfully.");
    }

}
