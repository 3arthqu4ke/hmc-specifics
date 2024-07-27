package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer implements Player {
    @Shadow
    public abstract void chat(String component);

    @Override
    public void sendMessage(String message) {
        this.chat(message);
    }

    @Override
    public void openMenu() {
        Minecraft.getInstance().setScreen(null);
        Minecraft.getInstance().pauseGame(false);
    }

    @Override
    public void closeScreen() {
        if (Minecraft.getInstance().screen != null) {
            Minecraft.getInstance().setScreen(null);
        }
    }

}
