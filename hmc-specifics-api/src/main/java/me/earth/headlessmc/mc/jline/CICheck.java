package me.earth.headlessmc.mc.jline;

import me.earth.headlessmc.logging.Logger;
import me.earth.headlessmc.logging.LoggerFactory;
import org.jline.terminal.impl.DumbTerminal;

import java.io.IOException;

/**
 * Checks if we are running on a CI runner.
 * E.g. Github action runners do not provide an interactive Terminal so JLine fails.
 */
public class CICheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewJLineProviders.class);

    public static Object ciCheck() {
        try {
            // there is probably some GITHUB env variable that we could also detect?
            // but on the other hand this will also work on non-github runners so its good
            Class.forName("me.earth.mc_runtime_test.McRuntimeTest");
            LOGGER.warn("Probably running inside CI, using dumb Terminal");
            return new DumbTerminal(System.in, System.out);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.warn("Failed to detect McRuntimeTest or open dumb Terminal", e);
        }

        return null;
    }

}
