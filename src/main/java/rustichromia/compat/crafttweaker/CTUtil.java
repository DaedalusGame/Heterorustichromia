package rustichromia.compat.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CTUtil {
    public static Ingredient toIngredient(IIngredient ingredient) {
        if(ingredient == null)
            return Ingredient.EMPTY;
        return new IngredientCraftTweaker(ingredient);
    }

    public static Collection<Ingredient> toIngredients(IIngredient[] ingredients) {
        return Arrays.stream(ingredients).map(CTUtil::toIngredient).collect(Collectors.toList());
    }

    public static Collection<ItemStack> toItemStacks(IItemStack[] stacks) {
        return Arrays.stream(stacks).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
    }
}