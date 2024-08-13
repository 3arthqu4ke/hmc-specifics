package me.earth.headlessmc.mc.mixins;

import com.google.gson.JsonElement;
import me.earth.headlessmc.mc.adventure.AdventureHelper;
import me.earth.headlessmc.mc.adventure.AdventureWrapper;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class MixinChatComponent {
    @Shadow @Final private static Logger LOGGER;

    @Unique
    private boolean hmc_adventureHelperInitialized = false;
    @Unique
    private AdventureHelper hmc_adventureHelper;

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;I)V",
        at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"), cancellable = true)
    private void logChatMessageHook(Component content, int id, CallbackInfo ci) {
        if (AdventureWrapper.ENABLED) {
            String ansiString = null;
            try {
                if (!hmc_adventureHelperInitialized) {
                    hmc_adventureHelper = AdventureWrapper.getAdventureHelper(k -> Language.getInstance().has(k), (s, def) -> Language.getInstance().getOrDefault(s));
                    hmc_adventureHelperInitialized = true;
                }

                if (hmc_adventureHelper != null) {
                    JsonElement json = Component.Serializer.toJsonTree(content);
                    ansiString = hmc_adventureHelper.toAnsiString(json);
                }
            } catch (Exception e) {
                if (AdventureWrapper.OUTPUT_THROWABLES) {
                    LOGGER.error("Failed to serialize {}", content.getString(), e);
                }
            }

            if (ansiString != null) {
                LOGGER.info("[CHAT] {}", ansiString);
                ci.cancel();
            }
        }
    }

}
