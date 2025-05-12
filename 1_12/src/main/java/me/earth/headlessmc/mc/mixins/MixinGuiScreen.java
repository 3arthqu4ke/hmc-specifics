package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.ComponentHelper;
import me.earth.headlessmc.mc.ReflectionHelper;
import me.earth.headlessmc.mc.gui.GuiButton;
import me.earth.headlessmc.mc.gui.GuiElement;
import me.earth.headlessmc.mc.gui.GuiScreen;
import me.earth.headlessmc.mc.gui.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(net.minecraft.client.gui.GuiScreen.class)
public abstract class MixinGuiScreen implements GuiScreen {
    @Shadow
    protected List<net.minecraft.client.gui.GuiButton> buttonList;

    @Shadow
    protected abstract void mouseClicked(int x, int y, int button);

    @Shadow
    protected abstract void mouseReleased(int x, int y, int button);

    @Override
    public void click(int x, int y, int button) {
        mouseClicked(x, y, button);
        mouseReleased(x, y, button);
    }

    @Override
    public List<GuiButton> getButtons() {
        Set<GuiButton> buttons = new HashSet<>();
        buttonList.stream()
                  .filter(GuiButton.class::isInstance)
                  .map(GuiButton.class::cast)
                  .forEach(buttons::add);
        buttons.addAll(ReflectionHelper.findAll(this, GuiButton.class));
        return new ArrayList<>(buttons);
    }

    @Override
    public List<TextField> getTextFields() {
        return new ArrayList<>(ReflectionHelper.findAll(this, TextField.class));
    }

    @Override
    public List<GuiElement> getAllElements() {
        Set<GuiElement> result = new LinkedHashSet<>();
        buttonList.stream()
                .filter(GuiElement.class::isInstance)
                .map(GuiElement.class::cast)
                .forEach(result::add);
        result.addAll(ReflectionHelper.findAll(this, GuiElement.class));
        //noinspection ConstantValue
        if (GuiContainer.class.isInstance(this)) {
            GuiContainer guiContainer = GuiContainer.class.cast(this);
            for (Slot slot : guiContainer.inventorySlots.inventorySlots) {
                ItemStack stack = slot.getStack();
                String text = "";
                if (!stack.isEmpty()) {
                    text = ComponentHelper.toAnsiString(stack.getTextComponent());
                }

                int xOffset = (guiContainer.width - 176) / 2;
                int yOffset = (guiContainer.height - 166) / 2;

                List<String> tooltip = hmc$getItemToolTip(stack);
                result.add(new me.earth.headlessmc.mc.gui.Slot(
                        text,
                        slot.xPos + xOffset,
                        slot.yPos + yOffset,
                        18,
                        18,
                        slot.slotNumber,
                        tooltip
                ));
            }

            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null) {
                // AbstractContainerScreen draggingItem is not accessible :(
                ItemStack carried = player.inventory.getItemStack();
                if (!carried.isEmpty()) {
                    String text = "";
                    if (!carried.isEmpty()) {
                        text = ComponentHelper.toAnsiString(carried.getTextComponent());
                    }

                    List<String> tooltip = hmc$getItemToolTip(carried);
                    result.add(new me.earth.headlessmc.mc.gui.Carried(
                            text,
                            0,
                            0,
                            18,
                            18,
                            result.size(),
                            tooltip
                    ));
                }
            }
        }

        return new ArrayList<>(result);
    }

    public List<String> hmc$getItemToolTip(ItemStack itemStack) {
        Minecraft mc = Minecraft.getMinecraft();
        List<String> list = itemStack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        for(int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, itemStack.getRarity().color + list.get(i));
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        List<String> result = new ArrayList<>(list.size());
        for (String string : list) {
            result.add(ComponentHelper.toAnsiString(string, string));
        }

        return result;
    }

}
