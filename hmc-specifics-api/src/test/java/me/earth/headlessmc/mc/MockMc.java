package me.earth.headlessmc.mc;

import me.earth.headlessmc.mc.auth.McAccount;
import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.keyboard.Keyboard;
import me.earth.headlessmc.mc.player.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public enum MockMc implements Minecraft {
    INSTANCE;

    public String ip;
    public int port = -1;

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public GuiScreen getScreen() {
        return null;
    }

    @Override
    public void quit() {

    }

    @Override
    public FontRenderer getFontRenderer() {
        return null;
    }

    @Override
    public Keyboard getKeyboard() {
        return null;
    }

    @Override
    public void connect(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void disconnect() {
        this.ip = null;
        this.port = -1;
    }

    @Override
    public McAccount getMcAccount() {
        return null;
    }

    @Override
    public void setMcAccount(McAccount account) {

    }

    @Override
    public Future<?> schedule(Runnable task) {
        task.run();
        return CompletableFuture.completedFuture(null);
    }

}
