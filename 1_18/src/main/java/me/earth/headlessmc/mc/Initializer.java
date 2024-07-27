package me.earth.headlessmc.mc;

import me.earth.headlessmc.command.line.CommandLineImpl;
import me.earth.headlessmc.config.ConfigImpl;
import me.earth.headlessmc.logging.LoggingHandler;
import me.earth.headlessmc.mc.commands.MinecraftContext;
import me.earth.headlessmc.runtime.Runtime;
import me.earth.headlessmc.runtime.RuntimeApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Initializer {
    private static final Logger LOGGER = LogManager.getLogger(Initializer.class);

    public static void init(Minecraft mc) throws IOException {
        LoggingHandler.apply();
        LOGGER.info("Loading HeadlessMc Runtime!");
        CommandLineImpl commandLine = new CommandLineImpl();
        if (RuntimeApi.getRuntime() != null) {
            // TODO: Compatibility CommandContext which allows you to specify
            //  in which context to execute the command.
            throw new IllegalStateException(
                "RuntimeApi has already been initialized!");
        }

        Runtime runtime = RuntimeApi.init(ConfigImpl.empty(), commandLine);
        reflectiveRuntimeCheck(runtime);
        runtime.setCommandContext(new MinecraftContext(runtime, mc));
        commandLine.listenAsync(runtime);
    }

    private static void reflectiveRuntimeCheck(Runtime runtime) {
        try {
            Class<?> clazz = Class.forName(RuntimeApi.class.getName(), false,
                                           ClassLoader.getSystemClassLoader());
            Method method = clazz.getMethod("getRuntime");
            method.setAccessible(true);
            Object alreadyLoaded = method.invoke(null);
            if (alreadyLoaded != null && !runtime.equals(alreadyLoaded)) {
                throw new IllegalStateException(
                    "RuntimeApi has already been loaded by!");
            }
        } catch (ClassNotFoundException
            | NoSuchMethodException
            | IllegalAccessException
            | InvocationTargetException ignored) {
        }
    }

}
