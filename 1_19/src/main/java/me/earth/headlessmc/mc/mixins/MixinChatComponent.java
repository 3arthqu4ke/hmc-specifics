package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.ComponentHelper;
import me.earth.headlessmc.mc.adventure.AdventureWrapper;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class MixinChatComponent {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    private void logChatMessageHook(Component content, @Nullable GuiMessageTag guiMessageTag, CallbackInfo ci) {
        if (AdventureWrapper.ENABLED) {
            String ansiString = ComponentHelper.toAnsiString(content);
            if (ansiString != null) {
                String logTag = Optionull.map(guiMessageTag, GuiMessageTag::logTag);
                if (logTag != null) {
                    LOGGER.info("[{}] [CHAT] {}", logTag, ansiString);
                } else {
                    LOGGER.info("[CHAT] {}", ansiString);
                }

                ci.cancel();
            }
        }
    }

}
