package me.earth.headlessmc.mc.mixins;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.earth.headlessmc.mc.adventure.AdventureHelper;
import me.earth.headlessmc.mc.adventure.AdventureWrapper;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.HoverEvent;
import org.slf4j.Logger;
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

    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    private void logChatMessageHook(GuiMessage message, CallbackInfo ci) {
        if (AdventureWrapper.ENABLED) {
            Component content = message.content();
            String ansiString = null;
            try {
                if (!hmc_adventureHelperInitialized) {
                    hmc_adventureHelper = AdventureWrapper.getAdventureHelper(k -> Language.getInstance().has(k), (s, def) -> Language.getInstance().getOrDefault(s, def));
                    hmc_adventureHelperInitialized = true;
                }

                if (hmc_adventureHelper != null) {
                    JsonElement json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, content).getOrThrow();
                    ansiString = hmc_adventureHelper.toAnsiString(json);
                }
            } catch (Exception e) {
                if (AdventureWrapper.OUTPUT_THROWABLES) {
                    if (e.getMessage().contains("Action not allowed: ")
                        || e.getMessage().contains("type not allowed: ")) {
                        ansiString = content.getString();
                    } else {
                        LOGGER.error("Failed to serialize {}", content.getString(), e);
                    }
                }
            }

            if (ansiString != null) {
                String logTag = Optionull.map(message.tag(), GuiMessageTag::logTag);
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
