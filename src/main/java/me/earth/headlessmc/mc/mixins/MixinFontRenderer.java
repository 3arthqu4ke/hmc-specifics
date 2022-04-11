package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.FontRendererListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(net.minecraft.client.gui.FontRenderer.class)
public abstract class MixinFontRenderer implements FontRenderer {
    private final List<FontRendererListener> listeners =
        new CopyOnWriteArrayList<>();

    @Override
    public void register(FontRendererListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregister(FontRendererListener listener) {
        listeners.remove(listener);
    }

    @Inject(method = "renderString", at = @At("HEAD"))
    private void onRender(String text, float x, float y, int color,
                          boolean dropShadow,
                          CallbackInfoReturnable<Integer> cir) {
        listeners.forEach(l -> l.onRender(text, x, y));
    }

}
