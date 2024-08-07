package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.FontRendererImpl;
import me.earth.headlessmc.mc.Initializer;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.player.Player;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@SuppressWarnings("MixinSuperClass")
@Mixin(net.minecraft.client.Minecraft.class)
public abstract class MixinMinecraft extends MixinBlockableEventLoop implements Minecraft {
    @Shadow
    public LocalPlayer player;
    @Shadow
    public Screen screen;
    @Shadow
    public ClientLevel level;

    @Shadow
    public abstract void stop();

    @Shadow
    public abstract void setScreen(Screen screen);

    @Shadow
    public abstract boolean isLocalServer();

    @Shadow
    public abstract void clearLevel(Screen screen);

    @Shadow public abstract void clearLevel();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(GameConfig config, CallbackInfo ci) throws IOException {
        Initializer.init(this);
    }

    @Override
    public Player getPlayer() {
        return (Player) player;
    }

    @Override
    public GuiScreen getScreen() {
        return (GuiScreen) screen;
    }

    @Override
    public void quit() {
        this.stop();
    }

    @Override
    public FontRenderer getFontRenderer() {
        return FontRendererImpl.INSTANCE;
    }

    @Override
    public void connect(String ip, int port) {
        this.disconnect();
        ServerAddress address = ServerAddress.parseString(ip + ":" + port);
        setScreen(
            new ConnectScreen(
                new TitleScreen(),
                net.minecraft.client.Minecraft.class.cast(this),
                new ServerData(
                    I18n.get("selectServer.defaultName"), address.toString(), false)));
    }

    @Override
    public void disconnect() {
        ClientLevel level = this.level;
        if (level != null) {
            level.disconnect();
        }

        if (this.isLocalServer()) {
            this.clearLevel(
                new GenericDirtMessageScreen(
                    new TranslatableComponent("menu.savingLevel")));
        } else {
            this.clearLevel();
        }

        this.setScreen(new TitleScreen());
    }

}
