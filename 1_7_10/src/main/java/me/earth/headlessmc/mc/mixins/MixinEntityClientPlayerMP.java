package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityClientPlayerMP.class)
public abstract class MixinEntityClientPlayerMP implements Player {
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;

    @Override
    public void sendMessage(String message) {
        this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
    }

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
