package me.earth.headlessmc.mc.adventure;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Adventure might not be available at runtime.
 * I have made bad experiences on forge with even loading classes that contain
 * references to classes not available, so I wrap it.
 *
 * @see AdventureHelperImpl
 */
public class AdventureWrapper {
    public static final boolean OUTPUT_THROWABLES = Boolean.parseBoolean(System.getProperty("hmc.adventure.output.throwables", "true"));
    public static final boolean USE_NONE_COLOR_LEVEL = Boolean.parseBoolean(System.getProperty("hmc.adventure.color.none", "false"));
    public static final boolean ENABLED = Boolean.parseBoolean(System.getProperty("hmc.adventure.enabled", "true"));

    public static @Nullable AdventureHelper getAdventureHelper(Function<String, Boolean> languageHasKey, BiFunction<String, String, String> languageGetOrDefault) {
        try {
            return new AdventureHelperImpl(languageHasKey, languageGetOrDefault);
        } catch (Throwable throwable) {
            if (OUTPUT_THROWABLES) {
                throwable.printStackTrace(System.err);
            }

            return null;
        }
    }

}
