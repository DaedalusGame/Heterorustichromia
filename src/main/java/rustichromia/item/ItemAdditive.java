package rustichromia.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemAdditive extends Item {
    int defaultAmount;

    public ItemAdditive(int defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public abstract String formatAmount(int amount);

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(formatAmount(getAmount(stack)));
        tooltip.add(I18n.format("rustichromia.tooltip.item.combine"));
    }

    public int getAmount(ItemStack stack) {
        if(stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            return compound.getInteger("amount");
        } else {
            return defaultAmount;
        }
    }

    public void setAmount(ItemStack stack, int amount) {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }

        if(amount == defaultAmount) {
            compound.removeTag("amount");
            if(compound.hasNoTags())
                stack.setTagCompound(null);
        } else {
            compound.setInteger("amount",amount);
        }
    }
}
