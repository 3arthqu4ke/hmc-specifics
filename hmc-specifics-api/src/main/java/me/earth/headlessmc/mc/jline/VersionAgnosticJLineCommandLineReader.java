package me.earth.headlessmc.mc.jline;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.process.InAndOutProvider;
import me.earth.headlessmc.jline.JLineCommandLineReader;
import me.earth.headlessmc.jline.JLineProperties;
import me.earth.headlessmc.logging.Logger;
import me.earth.headlessmc.logging.LoggerFactory;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.SpecificsInitializer;
import me.earth.headlessmc.mc.log4j.HMCLog4JAppender;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.OSUtils;

import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.jline.terminal.TerminalBuilder.PROP_CODEPAGE;

/**
 * (Neo)Forge puts JLine on the classpath itself.
 * They are also weird when it comes to ServiceLoaders so we gotta do some hacky stuff to get our JLine Terminal.
 */
public class VersionAgnosticJLineCommandLineReader extends JLineCommandLineReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecificsInitializer.class);
    private final AtomicReference<Throwable> failed = new AtomicReference<>();
    private final Minecraft mc;

    public VersionAgnosticJLineCommandLineReader(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    public void read(HeadlessMc hmc) throws IOError {
        try {
            super.read(hmc);
        } catch (Throwable throwable) {
            mc.schedule(() -> {
                throw new IllegalStateException("Exception on CommandLineThread", throwable);
            });

            throw throwable;
        }
    }

    @Override
    public synchronized void open(HeadlessMc hmc) throws IOException {
        try {
            if (!hmc.getConfig().get(JLineProperties.DUMB, false)) {
                System.setProperty(JLineProperties.FORCE_NOT_DUMB.getName(), "true");
            }

            super.open(hmc);
        } catch (Throwable throwable) {
            failed.set(throwable);
            throw throwable;
        } finally {
            synchronized (failed) {
                failed.notifyAll();
            }
        }
    }

    @Override
    protected Terminal buildTerminal(HeadlessMc hmc, boolean dumb, String providers, InAndOutProvider io) throws IOException {
        LOGGER.info("Building the Terminal with default TerminalBuilder...");
        try {
            return super.buildTerminal(hmc, dumb, providers, io);
        } catch (Throwable e) { // NoSuchMethodErrors if e.g. jni is not available
            LOGGER.warn("Failed to create terminal with default TerminalBuilder", e);
            try {
                return TerminalBuilder.builder().dumb(dumb).build();
            } catch (Throwable throwable) {
                LOGGER.error("Failed to create terminal with fallback TerminalBuilder", throwable);
                return hackInTerminal();
            }
        }
    }

    protected Terminal hackInTerminal() {
        // Forge packages JLine itself, but is very weird with ServiceLoaders
        // So we gotta hack a Terminal in ourselves.
        int codepage = 0;
        String str = System.getProperty(PROP_CODEPAGE);
        if (str != null) {
            codepage = Integer.parseInt(str);
        }

        if (OSUtils.IS_WINDOWS) {
            try {
                Object support = Class.forName("org.jline.terminal.impl.jna.JnaSupportImpl").getConstructor().newInstance();
                Method winSysTerminal = Class.forName("org.jline.terminal.spi.JnaSupport").getMethod("winSysTerminal", String.class, String.class, boolean.class, Charset.class, int.class, boolean.class, Terminal.SignalHandler.class);
                return (Terminal) winSysTerminal.invoke(support, "HeadlessMc-Terminal", "jna", true, StandardCharsets.UTF_8, codepage, false, Terminal.SignalHandler.SIG_DFL);
            } catch (Throwable t) {
                LOGGER.warn("Failed to create terminal through JnaSupportImpl", t);
            }
        }

        try {
            Object support = Class.forName("org.jline.terminal.impl.jansi.JansiSupportImpl").getConstructor().newInstance();
            Method winSysTerminal = Class.forName("org.jline.terminal.spi.JansiSupport").getMethod("winSysTerminal", String.class, String.class, boolean.class, Charset.class, int.class, boolean.class, Terminal.SignalHandler.class);
            return (Terminal) winSysTerminal.invoke(support, "HeadlessMc-Terminal", "jansi", true, StandardCharsets.UTF_8, codepage, false, Terminal.SignalHandler.SIG_DFL);
        } catch (Throwable t) {
            LOGGER.warn("Failed to create terminal through JansiSupportImpl", t);
        }

        return NewJLineProviders.provide();
    }

    public static void installAndReadAsyncAndAwaitException(Minecraft mc, HeadlessMc hmc) {
        VersionAgnosticJLineCommandLineReader jLineCommandLineReader = new VersionAgnosticJLineCommandLineReader(mc);
        hmc.getCommandLine().setCommandLineProvider(() -> jLineCommandLineReader);
        synchronized (jLineCommandLineReader.failed) {
            hmc.getCommandLine().readAsync(hmc);
            try {
                jLineCommandLineReader.failed.wait();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        if (jLineCommandLineReader.failed.get() != null) {
            throw new IllegalStateException("Failed to start JLineCommandLineReader", jLineCommandLineReader.failed.get());
        }

        HMCLog4JAppender.install(hmc.getCommandLine());
    }

}
