package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.FontRendererImpl;
import me.earth.headlessmc.mc.Initializer;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.player.Player;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(net.minecraft.client.Minecraft.class)
public abstract class MixinMinecraft extends MixinBlockableEventLoop
    implements Minecraft {
    @Shadow
    public LocalPlayer player;
    @Shadow
    public Screen screen;

    @Shadow
    public abstract void stop();

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

}
