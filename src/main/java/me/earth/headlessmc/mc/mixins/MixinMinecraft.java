package me.earth.headlessmc.mc.mixins;

import com.google.common.util.concurrent.ListenableFuture;
import me.earth.headlessmc.mc.Initializer;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.player.Player;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.main.GameConfiguration;
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
    public net.minecraft.client.gui.FontRenderer fontRenderer;

    @Shadow
    public abstract void shutdown();

    @Shadow
    public abstract ListenableFuture<Object> addScheduledTask(Runnable run);

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
        return (FontRenderer) fontRenderer;
    }

    @Override
    public Future<?> schedule(Runnable task) {
        return addScheduledTask(task);
    }

}
