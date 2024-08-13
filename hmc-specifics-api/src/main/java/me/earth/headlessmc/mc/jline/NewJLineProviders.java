package me.earth.headlessmc.mc.jline;

import me.earth.headlessmc.logging.Logger;
import me.earth.headlessmc.logging.LoggerFactory;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.jansi.JansiTerminalProvider;
import org.jline.terminal.impl.jna.JnaTerminalProvider;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

import java.nio.charset.StandardCharsets;

/**
 * In a separate class because these classes might not be available at runtime.
 */
public class NewJLineProviders {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewJLineProviders.class);

    public static Terminal provide() {
        LOGGER.warn("Last try, trying new JLine providers...");

        try {
            TerminalProvider terminalProvider = new JnaTerminalProvider();
            return terminalProvider.sysTerminal("HeadlessMc-Terminal", "jna", true, StandardCharsets.UTF_8, false, Terminal.SignalHandler.SIG_DFL, false, SystemStream.Output);
        } catch (Throwable t) {
            LOGGER.warn("Failed to create terminal through JnaTerminalProvider", t);
            if (t.getMessage().contains("Inappropriate ioctl for device")) {
                Object terminal = CICheck.ciCheck();
                if (terminal instanceof Terminal) {
                    return (Terminal) terminal;
                }
            }
        }

        try {
            TerminalProvider terminalProvider = new JansiTerminalProvider();
            return terminalProvider.sysTerminal("HeadlessMc-Terminal", "jansi", true, StandardCharsets.UTF_8, false, Terminal.SignalHandler.SIG_DFL, false, SystemStream.Output);
        } catch (Throwable t) {
            LOGGER.warn("Failed to create terminal through JansiTerminalProvider", t);
        }

        throw new IllegalStateException("Failed to call new JLineProviders!");
    }

}
