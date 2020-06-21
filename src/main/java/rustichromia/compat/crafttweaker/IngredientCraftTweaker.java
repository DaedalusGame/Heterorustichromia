package rustichromia.compat.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import rustichromia.util.IHasSize;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IngredientCraftTweaker extends Ingredient implements IHasSize {
    IIngredient predicate;

    public IngredientCraftTweaker(IIngredient ingredient)
    {
        predicate = ingredient;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        List<IItemStack> stacks = predicate != null ? predicate.getItems() : new ArrayList<>();
        return stacks.stream().map(CraftTweakerMC::getItemStack).map(stack -> withSize(stack,predicate.getAmount())).toArray(ItemStack[]::new);
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if(predicate == null)
            return stack.isEmpty();
        return predicate.matches(CraftTweakerMC.getIItemStack(stack));
    }

    @Override
    public int getSize() {
        return predicate.getAmount();
    }

    private ItemStack withSize(ItemStack stack, int size) {
        stack.setCount(size);
        return stack;
    }
}
