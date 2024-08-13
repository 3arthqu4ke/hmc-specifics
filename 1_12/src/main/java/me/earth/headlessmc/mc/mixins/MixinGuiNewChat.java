package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.adventure.AdventureHelper;
import me.earth.headlessmc.mc.adventure.AdventureWrapper;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
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
    private static Logger LOGGER;

    @Unique
    private boolean hmc_adventureHelperInitialized = false;
    @Unique
    private AdventureHelper hmc_adventureHelper;

    @Inject(method = "printChatMessageWithOptionalDeletion",
        at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"), cancellable = true)
    private void logChatMessageHook(ITextComponent content, int id, CallbackInfo ci) {
        if (AdventureWrapper.ENABLED) {
            String ansiString = null;
            try {
                if (!hmc_adventureHelperInitialized) {
                    hmc_adventureHelper = AdventureWrapper.getAdventureHelper(I18n::hasKey, (s, def) -> I18n.format(s));
                    hmc_adventureHelperInitialized = true;
                }

                if (hmc_adventureHelper != null) {
                    // OK FOR SOME REASON IT DOESNT WORK IN 1.12.2????????? Potentially due to JLine?
                    // I have no clue but I do not care for legacy enough rn
                    ansiString = hmc_adventureHelper.toAnsiStringLegacy(content.getFormattedText());
                }
            } catch (Exception e) {
                if (AdventureWrapper.OUTPUT_THROWABLES) {
                    LOGGER.error("Failed to serialize {}", content.getUnformattedText(), e);
                }
            }

            if (ansiString != null) {
                LOGGER.info("[CHAT] {}", ansiString);
                ci.cancel();
            }
        }
    }

}
