package rustichromia.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import rustichromia.tile.TileEntityRatiobox;

public class ContainerRatioBox extends Container {
    TileEntityRatiobox ratioBox;

    public ContainerRatioBox(EntityPlayer player, TileEntityRatiobox ratioBox) {
        this.ratioBox = ratioBox;

        bindPlayerInventory(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null));
    }

    protected void bindPlayerInventory(IItemHandler inventoryPlayer) {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                addSlotToContainer(new SlotItemHandler(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 82 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            addSlotToContainer(new SlotItemHandler(inventoryPlayer, i, 8 + i * 18, 82+58));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public void setRatio(double ratioOn, double ratioOff) {
        ratioBox.setRatio(ratioOn,ratioOff);
    }

    public double getRatioOn() {
        return ratioBox.getRatioOn();
    }

    public double getRatioOff() {
        return ratioBox.getRatioOff();
    }

    public float getAngleA(float partialTicks) {
        return (float) MathHelper.clampedLerp(ratioBox.aLastAngle,ratioBox.aAngle,partialTicks);
    }

    public float getAngleB(float partialTicks) {
        return (float) MathHelper.clampedLerp(ratioBox.bLastAngle,ratioBox.bAngle,partialTicks);
    }
}
