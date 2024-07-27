package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngameMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP implements Player {
    @Override
    @Invoker("sendChatMessage")
    public abstract void sendMessage(String message);

    @Override
    public void openMenu() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiIngameMenu());
    }

    @Override
    public void closeScreen() {
        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

}
