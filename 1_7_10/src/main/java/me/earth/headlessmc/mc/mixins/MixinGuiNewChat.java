package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.adventure.AdventureHelper;
import me.earth.headlessmc.mc.adventure.AdventureWrapper;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {
    @Shadow
    @Final
    private static Logger logger;

    @Unique
    private boolean hmc_adventureHelperInitialized = false;
    @Unique
    private AdventureHelper hmc_adventureHelper;

    @Inject(method = "printChatMessageWithOptionalDeletion",
        at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), cancellable = true)
    private void logChatMessageHook(IChatComponent content, int id, CallbackInfo ci) {
        if (AdventureWrapper.ENABLED) {
            String ansiString = null;
            try {
                if (!hmc_adventureHelperInitialized) {
                    hmc_adventureHelper = AdventureWrapper.getAdventureHelper(s -> true, (s, def) -> I18n.format(s));
                    hmc_adventureHelperInitialized = true;
                }

                if (hmc_adventureHelper != null) {
                    ansiString = hmc_adventureHelper.toAnsiStringLegacy(content.getFormattedText());
                }
            } catch (Exception e) {
                if (AdventureWrapper.OUTPUT_THROWABLES) {
                    logger.error("Failed to serialize {}", content.getUnformattedText(), e);
                }
            }

            if (ansiString != null) {
                //noinspection StringConcatenationArgumentToLogCall
                logger.info("[CHAT] " + ansiString);
                ci.cancel();
            }
        }
    }

}
