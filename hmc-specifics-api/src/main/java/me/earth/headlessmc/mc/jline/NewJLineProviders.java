package me.earth.headlessmc.mc.jline;

import me.earth.headlessmc.logging.Logger;
import me.earth.headlessmc.logging.LoggerFactory;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.jline.terminal.impl.jansi.JansiTerminalProvider;
import org.jline.terminal.impl.jna.JnaTerminalProvider;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

import java.io.IOException;
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
                try {
                    Class.forName("me.earth.mc_runtime_test.McRuntimeTest");
                    LOGGER.warn("Probably running inside CI, using dumb Terminal");
                    return new DumbTerminal(System.in, System.out);
                } catch (ClassNotFoundException | IOException e) {
                    LOGGER.warn("Failed to detect McRuntimeTest or open dumb Terminal", e);
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
