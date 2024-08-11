package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.gui.GuiButton;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.gui.TextField;
import me.earth.headlessmc.api.util.ReflectionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.lang.reflect.Field;
import java.util.*;

@Mixin(net.minecraft.client.gui.GuiScreen.class)
public abstract class MixinGuiScreen implements GuiScreen {
    @Shadow
    protected List<net.minecraft.client.gui.GuiButton> buttonList;

    @Override
    @Invoker("mouseClicked")
    public abstract void click(int x, int y, int button);

    @Override
    public List<GuiButton> getButtons() {
        Set<GuiButton> buttons = new HashSet<>();
        buttonList.stream()
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

    private <T> Set<T> findAll(Class<T> clazz) {
        Set<T> result = new HashSet<>();
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
