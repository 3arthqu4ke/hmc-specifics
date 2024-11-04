package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.gui.GuiButton;
import me.earth.headlessmc.mc.gui.GuiElement;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.gui.TextField;
import me.earth.headlessmc.mc.IdManager;
import me.earth.headlessmc.mc.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(Screen.class)
public abstract class MixinScreen implements GuiScreen, GuiEventListener {
    @Final
    @Shadow
    private List<Renderable> renderables;

    @Shadow public abstract void init(Minecraft minecraft, int i, int j);

    @Override
    public void click(int x, int y, int button) {
        mouseClicked(x, y, button);
    }

    @Override
    public List<GuiButton> getButtons() {
        Set<GuiButton> buttons = new LinkedHashSet<>();
        renderables.stream()
                   .filter(GuiButton.class::isInstance)
                   .map(GuiButton.class::cast)
                   .forEach(buttons::add);
        buttons.addAll(ReflectionHelper.findAll(this, GuiButton.class));
        //noinspection ConstantValue
        if (AbstractContainerScreen.class.isInstance(this)) {
            AbstractContainerScreen.class.cast(this).getMenu().slots.stream().map(GuiButton.class::cast).forEach(buttons::add);
        }

        return new ArrayList<>(buttons);
    }

    @Override
    public List<TextField> getTextFields() {
        return new ArrayList<>(ReflectionHelper.findAll(this, TextField.class));
    }

    @Override
    public List<GuiElement> getAllElements() {
        Set<GuiElement> result = new LinkedHashSet<>();
        renderables.stream()
                   .filter(GuiElement.class::isInstance)
                   .map(GuiElement.class::cast)
                   .forEach(result::add);
        result.addAll(ReflectionHelper.findAll(this, GuiElement.class));
        return new ArrayList<>(result);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initHook(Component component, CallbackInfo ci) {
        IdManager.INSTANCE.setContext(this);
    }

}
