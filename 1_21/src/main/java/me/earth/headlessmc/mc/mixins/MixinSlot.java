package me.earth.headlessmc.mc.mixins;

import me.earth.headlessmc.mc.IdManager;
import me.earth.headlessmc.mc.gui.GuiButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Slot.class)
public abstract class MixinSlot implements GuiButton {
    @Shadow @Final public int x;
    @Shadow @Final public int y;
    @Unique
    private Integer hmc_specifics$id;

    @Shadow
    public abstract ItemStack getItem();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public String getText() {
        // TODO:
        return getItem().getDescriptionId();
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getId() {
        if (hmc_specifics$id == null) {
            this.hmc_specifics$id = IdManager.INSTANCE.getNextId();
        }

        return hmc_specifics$id;
    }

}
