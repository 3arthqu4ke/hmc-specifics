package me.earth.headlessmc.mc;

import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.player.Player;
import me.earth.headlessmc.mc.scheduling.SchedulesTasks;

/**
 * Represents the Minecraft object.
 */
public interface Minecraft extends SchedulesTasks, Adapter {
    /**
     * Returns the Minecraft player.
     *
     * @return a {@link Player} or <tt>null</tt> if we are currently not ingame.
     */
    Player getPlayer();

    /**
     * Retrieves the current {@link GuiScreen} Minecraft is displaying.
     *
     * @return the current GuiScreen or <tt>null</tt> if there's no GuiScreen.
     */
    GuiScreen getScreen();

    /**
     * Quits Minecraft, should be preferred over any {@link System#exit(int)}
     * calls to allow Minecraft to save worlds etc.
     */
    void quit();

    /**
     * @return a {@link FontRenderer} representing Minecraft's FontRenderer. The
     * FontRenderer will not be present if the game hasn't been launched with an
     * instrumentation.
     */
    FontRenderer getFontRenderer();

    /**
     * Connects you to the server with the given Ip and port.
     *
     * @param ip the ip of the server to connect to.
     * @param port the port of the server to connect to.
     */
    void connect(String ip, int port);

    /**
     * Disconnects you if you are connected to a server.
     */
    void disconnect();

}
