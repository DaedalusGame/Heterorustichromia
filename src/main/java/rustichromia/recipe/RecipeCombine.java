package rustichromia.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import rustichromia.item.ItemAdditive;

public class RecipeCombine extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack firstStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(stack.isEmpty())
                continue;
            if(!(stack.getItem() instanceof ItemAdditive))
                return false;
            else if(firstStack.isEmpty())
                firstStack = stack;
            else if(firstStack.getItem() != stack.getItem())
                return false;
        }
        return !firstStack.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack firstStack = ItemStack.EMPTY;
        int amount = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(stack.isEmpty())
                continue;
            ItemAdditive additive = (ItemAdditive) stack.getItem();
            amount += additive.getAmount(stack);
            if(firstStack.isEmpty())
                firstStack = stack.copy();
        }
        ItemAdditive additive = (ItemAdditive) firstStack.getItem();
        additive.setAmount(firstStack,amount);
        firstStack.setCount(1);
        return firstStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}
