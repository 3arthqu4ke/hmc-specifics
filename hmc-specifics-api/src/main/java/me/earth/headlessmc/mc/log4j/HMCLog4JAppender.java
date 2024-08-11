package me.earth.headlessmc.mc.log4j;

import me.earth.headlessmc.api.classloading.Deencapsulator;
import me.earth.headlessmc.api.process.InAndOutProvider;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.jline.terminal.Terminal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Uses an {@link AppenderPrinter} to always print above the {@link org.jline.reader.LineReader}.
 * That we we get a persistent input line, even if the program puts out lots of text asynchronously.
 * @see <a href=https://github.com/Minecrell/TerminalConsoleAppender>https://github.com/Minecrell/TerminalConsoleAppender</a>
 */
// Core.CATEGORY_NAME and Appender.ELEMENT_TYPE are constants that do not exist on older versions of Log4J but its no problem because they are constants and get inlined
@Plugin(name = HMCLog4JAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class HMCLog4JAppender extends AbstractAppender {
    public static final String PLUGIN_NAME = "HMCLog4JAppender";
    private static final InAndOutProvider SYS_OUT = new InAndOutProvider();

    private static final Logger LOGGER = LogManager.getLogger(HMCLog4JAppender.class);
    private final AppenderPrinter appenderPrinter;

    @SuppressWarnings("deprecation") // we need to call the deprecated super-constructor, the non-deprecated one might not exist
    protected HMCLog4JAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, AppenderPrinter appenderPrinter) {
        super(name, filter, layout, ignoreExceptions/*, Property.EMPTY_ARRAY*/);
        this.appenderPrinter = appenderPrinter;
    }

    @Override
    public void append(LogEvent event) {
        synchronized (this) {
            appenderPrinter.printAboveLineReader(getLayout().toSerializable(event).toString());
        }
    }

    public static void install(Object commandLine) {
        Deencapsulator deencapsulator = new Deencapsulator();
        Optional<AppenderPrinter> appenderPrinter = new AppenderPrinter.Factory().getAppenderPrinter(deencapsulator, commandLine);
        if (!appenderPrinter.isPresent()) {
            LOGGER.error("Failed to install " + PLUGIN_NAME + ", AppenderPrinter not present");
            return;
        }

        closeTerminalConsoleAppender(deencapsulator);
        LOGGER.info(System.out);
        LOGGER.info(System.out.getClass().getClassLoader());
        LOGGER.info(HMCLog4JAppender.class.getClassLoader());
        LoggerContextFactory logFactory = LogManager.getFactory();
        if (logFactory instanceof Log4jContextFactory) {
            ((Log4jContextFactory) logFactory).getSelector().getLoggerContexts().forEach(context -> install(context, appenderPrinter.get()));
        }
    }

    // this is kinda gross and I am not sure what I am doing but it works!
    @SuppressWarnings("RedundantSuppression") // RedundantArrayCreation
    private static void install(LoggerContext context, AppenderPrinter appenderPrinter) {
        Configuration config = context.getConfiguration();
        LoggerConfig rootLoggerConfig;
        try {
            rootLoggerConfig = config.getRootLogger();
        } catch (Throwable throwable) { // older versions of Log4J do not have this method
            LOGGER.debug("Failed to access config.getRootLogger", throwable);
            rootLoggerConfig = config.getLoggerConfig("");
        }

        Map<String, Appender> appenders = new HashMap<>(config.getAppenders());
        try {
            Layout<?> layout = null;
            for (Map.Entry<String, Appender> entry : appenders.entrySet()) {
                String name = entry.getKey();
                Appender appender = entry.getValue();
                Layout<?> appenderLayout = appender.getLayout();
                // we have to specifically tell Log4J which method to use, or else this happens:
                // Caused by: java.lang.NoSuchMethodError: org.apache.logging.log4j.Logger.info(Ljava/lang/String;<4x Ljava/lang/Object;>)V
                //noinspection RedundantArrayCreation
                LOGGER.info("{}: {}, {}, {}", new Object[]{ name, appender, appender.getClass(),
                    appenderLayout instanceof PatternLayout ? ((PatternLayout) appenderLayout).getConversionPattern() : appenderLayout });
                if (appenderLayout instanceof PatternLayout && !(((PatternLayout) appenderLayout).getConversionPattern()).contains("nolookups")) {
                    for (int i = 0; i < 100; i++) {
                        LOGGER.fatal("PatternLayout does not contain {nolookups}!!!!!!!!!! You are potentially vulnerable to the Log4J exploit!");
                    }
                }

                // its possible that there is a TerminalConsoleAppender, should we hijack that instead of removing it
                if (appender instanceof ConsoleAppender || "Console".equals(appender.getName())) {
                    if ("TerminalConsoleAppender".equals(appender.getClass().getSimpleName())) {
                        LOGGER.info("There is a TerminalConsoleAppender installed!");
                        LOGGER.info(TerminalConsoleAppender.class.getClassLoader());
                        LOGGER.info(appender.getClass().getClassLoader());
                    }

                    //noinspection RedundantArrayCreation
                    LOGGER.info("Removing appender {}", new Object[]{ name });
                    rootLoggerConfig.removeAppender(name);
                    layout = appenderLayout;
                }
            }

            if (layout == null) {
                LOGGER.error("Failed to remove an Appender! Cannot install " + PLUGIN_NAME);
                return;
            }

            // layout = PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss}] [%t/%level] [%logger]: %msg{nolookups}%n").build();
            HMCLog4JAppender appender = new HMCLog4JAppender(PLUGIN_NAME, null, layout, true, appenderPrinter);
            appender.start();

            try {
                config.addAppender(appender);
            } catch (Throwable throwable) { // older versions of Log4J do not have this method
                LOGGER.debug("Failed to access config.addAppender", throwable);
            }

            rootLoggerConfig.addAppender(appender, Level.INFO, null);
            context.updateLoggers();
        } catch (Throwable throwable) {
            SYS_OUT.getErr().get().println("Failed to add " + PLUGIN_NAME);
            throwable.printStackTrace(SYS_OUT.getErr().get());
            try {
                Map<String, Appender> appendersNow = new HashMap<>(config.getAppenders());
                for (Map.Entry<String, Appender> entry : appenders.entrySet()) {
                    if (!appendersNow.containsKey(entry.getKey())) {
                        rootLoggerConfig.addAppender(entry.getValue(), Level.DEBUG, null);
                    }
                }

                context.updateLoggers();
            } catch (Throwable throwable2) {
                SYS_OUT.getErr().get().println("Failed to reinstall Log4JAppenders");
                throwable.printStackTrace(SYS_OUT.getErr().get());
            }
        }
    }

    private static void closeTerminalConsoleAppender(Deencapsulator deencapsulator) {
        //noinspection RedundantSuppression
        try {
            deencapsulator.deencapsulate(TerminalConsoleAppender.class);
            Terminal terminal = TerminalConsoleAppender.getTerminal();
            //noinspection RedundantArrayCreation
            LOGGER.info("TerminalConsoleAppender Terminal: {} : {}", new Object[]{terminal, (terminal == null ? "null" : terminal.getType())});
            TerminalConsoleAppender.close();
        } catch (Throwable t) {
            LOGGER.debug("Failed to close TerminalConsoleAppender", t);
        }
    }

}
