package me.earth.headlessmc.specifics;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;

@SuppressWarnings("unused")
public class HeadlessMcMixinConnector implements IMixinConnector {
    @Override
    public void connect() {
        Mixins.addConfiguration("mixins.headlessmc.json");
    }

}
