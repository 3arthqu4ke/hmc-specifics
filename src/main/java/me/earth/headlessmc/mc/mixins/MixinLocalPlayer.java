package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer implements Player {
    @Shadow
    public abstract void commandSigned(String string, Component arg);

    @Shadow
    public abstract void chatSigned(String string, Component arg);

    @Override
    public void sendMessage(String message) {
        // TODO: component?
        if (message.startsWith("/")) {
            this.commandSigned(message.substring(1), null);
        } else {
            this.chatSigned(message, null);
        }
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
