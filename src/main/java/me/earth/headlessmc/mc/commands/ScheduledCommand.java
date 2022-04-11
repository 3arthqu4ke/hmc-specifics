package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.command.Command;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.mc.Minecraft;

/**
 * {@link Command}s implementing this interface will be scheduled on Minecrafts
 * main thread via {@link Minecraft#schedule(Runnable)} if registered on an
 * {@link MinecraftContext}. Any {@link CommandException}s thrown by this
 * command will still be caught, but be aware that other Exceptions will crash
 * Minecraft!
 */
public interface ScheduledCommand {
}
