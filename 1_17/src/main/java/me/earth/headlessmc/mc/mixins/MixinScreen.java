package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.IdManager;
import me.earth.headlessmc.mc.gui.GuiButton;
import me.earth.headlessmc.mc.gui.GuiElement;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.gui.TextField;
import me.earth.headlessmc.api.util.ReflectionUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.*;

@Mixin(Screen.class)
public abstract class MixinScreen implements GuiScreen, GuiEventListener {
    @Final
    @Shadow
    private List<AbstractWidget> renderables;

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
        buttons.addAll(findAll(GuiButton.class));
        return new ArrayList<>(buttons);
    }

    @Override
    public List<TextField> getTextFields() {
        return new ArrayList<>(findAll(TextField.class));
    }

    @Override
    public List<GuiElement> getAllElements() {
        Set<GuiElement> result = new LinkedHashSet<>();
        renderables.stream()
                   .filter(GuiElement.class::isInstance)
                   .map(GuiElement.class::cast)
                   .forEach(result::add);
        result.addAll(findAll(GuiElement.class));
        return new ArrayList<>(result);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initHook(Component component, CallbackInfo ci) {
        IdManager.INSTANCE.setContext(this);
    }

    private <T> Set<T> findAll(Class<T> clazz) {
        Set<T> result = new LinkedHashSet<>();
        ReflectionUtil.iterate(this.getClass(), c -> {
            for (Field field : c.getDeclaredFields()) {
                if (clazz.isAssignableFrom(field.getType())
                    || Arrays.stream(field.getType().getInterfaces())
                             .anyMatch(clazz::isAssignableFrom)) {
                    field.setAccessible(true);
                    try {
                        Object obj = field.get(this);
                        if (clazz.isInstance(obj)) {
                            result.add(clazz.cast(obj));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return result;
    }

}
