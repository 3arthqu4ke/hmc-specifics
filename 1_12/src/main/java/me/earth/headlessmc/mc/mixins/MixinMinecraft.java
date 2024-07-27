package me.earth.headlessmc.mc.mixins;

import com.google.common.util.concurrent.ListenableFuture;
import me.earth.headlessmc.mc.FontRendererImpl;
import me.earth.headlessmc.mc.Initializer;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.player.Player;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.realms.RealmsBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.concurrent.Future;

@Mixin(net.minecraft.client.Minecraft.class)
public abstract class MixinMinecraft implements Minecraft {
    @Shadow
    public EntityPlayerSP player;
    @Shadow
    public net.minecraft.client.gui.GuiScreen currentScreen;
    @Shadow
    public WorldClient world;

    @Shadow
    public abstract void shutdown();
    @Shadow
    public abstract ListenableFuture<Object> addScheduledTask(Runnable run);
    @Shadow
    public abstract boolean isIntegratedServerRunning();
    @Shadow
    public abstract boolean isConnectedToRealms();
    @Shadow
    public abstract void loadWorld(WorldClient worldClientIn);
    @Shadow
    public abstract void displayGuiScreen(
        net.minecraft.client.gui.GuiScreen guiScreenIn);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(GameConfiguration gameConfig, CallbackInfo ci)
        throws IOException {
        Initializer.init(this);
    }

    @Override
    public Player getPlayer() {
        return (Player) player;
    }

    @Override
    public GuiScreen getScreen() {
        return (GuiScreen) currentScreen;
    }

    @Override
    public void quit() {
        this.shutdown();
    }

    @Override
    public FontRenderer getFontRenderer() {
        return FontRendererImpl.INSTANCE;
    }

    @Override
    public Future<?> schedule(Runnable task) {
        return addScheduledTask(task);
    }

    @Override
    public void connect(String ip, int port) {
        this.disconnect();
        this.displayGuiScreen(new GuiConnecting(
            new GuiMainMenu(), net.minecraft.client.Minecraft.class.cast(this),
            ip, port));
    }

    @Override
    public void disconnect() {
        boolean singlePlayer = this.isIntegratedServerRunning();
        boolean realms = this.isConnectedToRealms();

        if (world != null) {
            world.sendQuittingDisconnectingPacket();
        }

        this.loadWorld(null);

        if (singlePlayer) {
            this.displayGuiScreen(new GuiMainMenu());
        } else if (realms) {
            new RealmsBridge().switchToRealms(new GuiMainMenu());
        } else {
            this.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
    }

}
