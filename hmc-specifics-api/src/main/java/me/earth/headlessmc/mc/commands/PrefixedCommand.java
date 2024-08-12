package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.mc.Minecraft;

import java.util.Locale;

// TODO: unit test
public abstract class PrefixedCommand extends AbstractMinecraftCommand {
    private final String prefix;
    private final String message;

    public PrefixedCommand(HeadlessMc ctx, Minecraft mc, String name, String desc, String prefix, String message) {
        super(ctx, mc, name, desc);
        this.prefix = prefix;
        this.message = message;
    }

    protected abstract void executeUnprefixed(String line, String unprefixedLine, String... args);

    @Override
    public void execute(String line, String... args) throws CommandException {
        String unprefixedLine = line;
        if (line.toLowerCase(Locale.ENGLISH).startsWith(getPrefix().toLowerCase(Locale.ENGLISH))) {
            unprefixedLine = line.substring(getPrefix().length());
        }

        if (unprefixedLine.isEmpty()) {
            throw new CommandException("Please specify a " + getMessage());
        }

        executeUnprefixed(line, unprefixedLine, args);
    }

    @Override
    public boolean matches(String line, String... args) {
        String lowerLine = line.toLowerCase(Locale.ENGLISH);
        return lowerLine.startsWith(getPrefix().toLowerCase(Locale.ENGLISH))
            || lowerLine.startsWith(getName().toLowerCase(Locale.ENGLISH));
    }

    public String getMessage() {
        return message;
    }

    public String getPrefix() {
        return prefix;
    }

}
