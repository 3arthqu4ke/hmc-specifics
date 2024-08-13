package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.api.command.CommandUtil;
import me.earth.headlessmc.auth.AbstractLoginCommand;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.auth.McAccount;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;

import java.util.UUID;

public class LoginCommand extends AbstractLoginCommand {
    protected final Minecraft mc;

    public LoginCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx);
        this.mc = mc;
    }

    @Override
    public void execute(String line, String... args) throws CommandException {
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

    @Override
    protected void onSuccessfulLogin(StepFullJavaSession.FullJavaSession fullJavaSession) {
        McAccount mcAccount = new McAccount(fullJavaSession.getMcProfile().getName(),
                                            fullJavaSession.getMcProfile().getId(),
                                            fullJavaSession.getMcProfile().getMcToken().getAccessToken());
        mc.setMcAccount(mcAccount);
        ctx.log("Logged in with " + mcAccount + " successfully.");
    }

}
