package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.gui.TextField;

public class TextCommand extends AbstractGuiCommand
    implements ScheduledCommand {
    public TextCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "text", "Set text in text fields.");
    }

    @Override
    protected void execute(GuiScreen gui, String... args)
        throws CommandException {
        if (args.length < 2) {
            throw new CommandException("Please specify an id!");
        }

        for (TextField textField : gui.getTextFields()) {
            if (args[1].equals(String.valueOf(textField.getId()))) {
                // TODO: option to use raw String instead of args[2]?
                String text = args.length < 3 ? "" : args[2];
                ctx.log(String.format("Setting text field %s to \"%s\".",
                                      args[1], text));
                textField.setText(text);
                return;
            }
        }

        throw new CommandException(String.format(
            "Couldn't find text field with id %s", args[1]));
    }

}

