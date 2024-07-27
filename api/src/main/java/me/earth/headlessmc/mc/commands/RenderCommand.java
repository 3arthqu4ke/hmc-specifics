package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.command.CommandUtil;
import me.earth.headlessmc.command.ParseUtil;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.FontRendererListener;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RenderCommand extends AbstractMinecraftCommand {
    public RenderCommand(HeadlessMc ctx, Minecraft mc) {
        super(ctx, mc, "render", "Outputs all text rendered on the screen.");
    }

    @Override
    public void execute(String... args) throws CommandException {
        FontRenderer fontRenderer = mc.getFontRenderer();

        long time = 100L;
        if (args.length > 1
            && !args[1].equalsIgnoreCase("-f")
            && !args[1].equalsIgnoreCase("-t")) {
            time = ParseUtil.parseL(args[1]);
            if (time < 0) {
                throw new CommandException("Time must be > 0!");
            }
        }

        boolean noFilter = CommandUtil.hasFlag("-f", args);
        Set<String> f = Collections.newSetFromMap(new ConcurrentHashMap<>());
        FontRendererListener listener = (text, x, y) -> {
            if (noFilter || f.add(text)) {
                ctx.log(String.format("{x=%s, y=%s, text=%s}", x, y, text));
            }
        };

        Runnable stopListening = stopListening(fontRenderer, listener, time);
        if (CommandUtil.hasFlag("-t", args)) {
            stopListening.run();
        } else {
            Thread thread = new Thread(stopListening);
            thread.setDaemon(true);
            thread.setName(String.format("RenderListener Exit %sms", time));
            thread.start();
        }
    }

    private Runnable stopListening(FontRenderer fontRenderer,
                                   FontRendererListener listener,
                                   long time) {
        return () -> {
            try {
                fontRenderer.register(listener);
                Thread.sleep(time);
            } catch (InterruptedException e) {
                ctx.log(Thread.currentThread().getName()
                            + " has been interrupted!");
                Thread.currentThread().interrupt();
            } finally {
                fontRenderer.unregister(listener);
            }
        };
    }

}
